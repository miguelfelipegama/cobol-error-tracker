package com.bank.errorapi.controller;

import com.bank.errorapi.dto.AggregatedMetric;
import com.bank.errorapi.dto.DashboardTotals;
import com.bank.errorapi.dto.ErrorDetailDto;
import com.bank.errorapi.entity.CobolError;
import com.bank.errorapi.entity.ErrorVariable;
import com.bank.errorapi.repository.CobolErrorRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@Path("/api/v1/metrics")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardController {

    @Inject
    CobolErrorRepository errorRepository;

    @GET
    @Path("/errors")
    public List<CobolError> getErrors(
            @QueryParam("programName") String programName,
            @QueryParam("creditObject") String creditObject) {

        StringBuilder query = new StringBuilder("1 = 1");
        Parameters params = new Parameters();

        if (programName != null && !programName.isBlank()) {
            query.append(" and programName = :programName");
            params.and("programName", programName);
        }

        if (creditObject != null && !creditObject.isBlank()) {
            query.append(" and correlation.creditObjectNumber = :creditObject");
            params.and("creditObject", creditObject);
        }

        return errorRepository.find(query.toString(), Sort.by("timestamp").descending(), params).list();
    }

    @GET
    @Path("/errors/{id}/detail")
    public ErrorDetailDto getErrorDetail(@PathParam("id") String id) {
        CobolError error = errorRepository.findById(id);
        if (error == null) {
            throw new NotFoundException("Error with id " + id + " not found");
        }

        // Load variables for this specific error
        List<ErrorVariable> variables = errorRepository.getEntityManager()
            .createQuery("select v from ErrorVariable v where v.errorId = :errorId order by v.sequence asc",
                         ErrorVariable.class)
            .setParameter("errorId", id)
            .getResultList();

        // Load all related errors sharing the same correlation, ordered oldest first
        List<CobolError> relatedErrors = errorRepository
            .find("correlation.id = :corrId order by timestamp asc",
                  Parameters.with("corrId", error.getCorrelation().getId()))
            .list();

        return new ErrorDetailDto(error, error.getCorrelation(), variables, relatedErrors);
    }

    @GET
    @Path("/totals")
    public DashboardTotals getTotals() {
        long totalErrors = errorRepository.count();
        long distinctObjects = (Long) errorRepository.getEntityManager()
            .createQuery("select count(distinct e.correlation.creditObjectNumber) from CobolError e")
            .getSingleResult();
        long distinctPrograms = (Long) errorRepository.getEntityManager()
            .createQuery("select count(distinct e.programName) from CobolError e")
            .getSingleResult();
        return new DashboardTotals(totalErrors, distinctObjects, distinctPrograms);
    }

    @GET
    @Path("/by-day")
    public List<AggregatedMetric> getErrorsByDay() {
        List<Object[]> results = errorRepository.getEntityManager()
            .createQuery("select function('to_char', e.timestamp, 'YYYY-MM-DD') as day, count(e) " +
                         "from CobolError e group by function('to_char', e.timestamp, 'YYYY-MM-DD') " +
                         "order by day", Object[].class)
            .getResultList();
        return results.stream()
            .map(r -> new AggregatedMetric((String) r[0], (Long) r[1]))
            .collect(Collectors.toList());
    }

    @GET
    @Path("/by-hour")
    public List<AggregatedMetric> getErrorsByHour() {
        List<Object[]> results = errorRepository.getEntityManager()
            .createQuery("select function('to_char', e.timestamp, 'HH24:00') as hour, count(e) " +
                         "from CobolError e group by function('to_char', e.timestamp, 'HH24:00') " +
                         "order by hour", Object[].class)
            .getResultList();
        return results.stream()
            .map(r -> new AggregatedMetric((String) r[0], (Long) r[1]))
            .collect(Collectors.toList());
    }
}
