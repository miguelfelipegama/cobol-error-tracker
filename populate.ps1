param()

$ProducerUrl = "http://localhost:8080/api/v1/errors"
$Results = [System.Collections.Generic.List[PSObject]]::new()

function Send-Error {
    param([hashtable]$payload)
    $json = $payload | ConvertTo-Json -Depth 5
    $elapsed = $null
    $success = $false

    $elapsed = (Measure-Command {
        try {
            $response = Invoke-WebRequest -Uri $ProducerUrl -Method POST -Body $json -ContentType "application/json" -UseBasicParsing
            $success = $true
        } catch {
            $success = $false
        }
    }).TotalMilliseconds

    $status = if ($success) { "OK" } else { "FAIL" }
    $elapsedStr = "{0:N2}ms" -f $elapsed

    Write-Host ("  [{0}] {1,-16} {2,-20} => {3}" -f $status, $payload.programName, $payload.errorCode, $elapsedStr)

    $Results.Add([PSCustomObject]@{
        Program  = $payload.programName
        Error    = $payload.errorCode
        Group    = $payload.creditObjectNumber
        Ms       = [Math]::Round($elapsed, 2)
        Success  = $success
    })

    Start-Sleep -Milliseconds 50
}

Write-Host ""
Write-Host "COBOL Error Tracker -- Benchmark de Tempo de Resposta (error-producer)"
Write-Host ("-" * 72)

# --- GRUPO 1: Cadeia de falha no contrato CONTRACT-001 ---
Write-Host ">>> Grupo 1: Cadeia CONTRACT-001"

Send-Error @{
    cicsCpu = "CICSPA"; taskCode = "CICS-0001"
    creditObjectNumber = "CONTRACT-001"; creditObjectType = "CONTRATO"
    programName = "PGM-VALIDA"; errorCode = "SQLCODE -811"
    errorMessage = "Multiplas linhas retornadas no SELECT"
    variables = @(@{ name = "WS-SQLCODE"; value = "-811" }, @{ name = "WS-TABLE"; value = "TB_CONTRATO" })
}
Send-Error @{
    cicsCpu = "CICSPA"; taskCode = "CICS-0001"
    creditObjectNumber = "CONTRACT-001"; creditObjectType = "CONTRATO"
    programName = "PGM-CALCULA"; errorCode = "S0C7"
    errorMessage = "Data exception - campo nao numerico"
    variables = @(@{ name = "WS-VALOR"; value = "SPACES" }, @{ name = "WS-PARCELAS"; value = "0" })
}
Send-Error @{
    cicsCpu = "CICSPA"; taskCode = "CICS-0001"
    creditObjectNumber = "CONTRACT-001"; creditObjectType = "CONTRATO"
    programName = "PGM-GRAVA-MOV"; errorCode = "SQLCODE -530"
    errorMessage = "INSERT violou restricao de chave estrangeira"
    variables = @(@{ name = "WS-SQLCODE"; value = "-530" }, @{ name = "WS-FK"; value = "CONTRACT-001" })
}
Send-Error @{
    cicsCpu = "CICSPA"; taskCode = "CICS-0001"
    creditObjectNumber = "CONTRACT-001"; creditObjectType = "CONTRATO"
    programName = "PGM-ROLLBACK"; errorCode = "SQLCODE -911"
    errorMessage = "Deadlock - transacao revertida"
    variables = @(@{ name = "WS-SQLCODE"; value = "-911" }, @{ name = "WS-REASON"; value = "68" })
}

Write-Host ""
Write-Host ">>> Grupo 2: Proposta PROP-7788"

Send-Error @{
    cicsCpu = "BATCH01"; taskCode = "JOB-AVPROP"
    creditObjectNumber = "PROP-7788"; creditObjectType = "PROPOSTA"
    programName = "PGM-LE-PROP"; errorCode = "SOC4"
    errorMessage = "Protection exception ao ler dataset"
    variables = @(@{ name = "WS-FILE-STATUS"; value = "37" })
}
Send-Error @{
    cicsCpu = "BATCH01"; taskCode = "JOB-AVPROP"
    creditObjectNumber = "PROP-7788"; creditObjectType = "PROPOSTA"
    programName = "PGM-SCORE"; errorCode = "S0C7"
    errorMessage = "Data exception no calculo de score"
    variables = @(@{ name = "WS-RENDA"; value = "HIGH-VALUE" }, @{ name = "WS-RETRY"; value = "1" })
}
Send-Error @{
    cicsCpu = "BATCH01"; taskCode = "JOB-AVPROP"
    creditObjectNumber = "PROP-7788"; creditObjectType = "PROPOSTA"
    programName = "PGM-SCORE"; errorCode = "S0C7"
    errorMessage = "Data exception no calculo de score - retentativa 2"
    variables = @(@{ name = "WS-RENDA"; value = "HIGH-VALUE" }, @{ name = "WS-RETRY"; value = "2" })
}
Send-Error @{
    cicsCpu = "BATCH01"; taskCode = "JOB-AVPROP"
    creditObjectNumber = "PROP-7788"; creditObjectType = "PROPOSTA"
    programName = "PGM-NOTIFICA"; errorCode = "FILE NOT FOUND"
    errorMessage = "VSAM dataset de notificacao ausente"
    variables = @(@{ name = "WS-VSAM"; value = "NOTIF.BATCH.PROP" })
}

Write-Host ""
Write-Host ">>> Grupo 3: Cliente CLI-330012"

Send-Error @{
    cicsCpu = "CICSPB"; taskCode = "TRAN-CLIE"
    creditObjectNumber = "CLI-330012"; creditObjectType = "CLIENTE"
    programName = "PGM-CONS-CLI"; errorCode = "SQLCODE -904"
    errorMessage = "Tablespace em recover - recurso indisponivel"
    variables = @(@{ name = "WS-SQLCODE"; value = "-904" }, @{ name = "WS-TS"; value = "DSNDB06.SYSSTR" })
}
Send-Error @{
    cicsCpu = "CICSPB"; taskCode = "TRAN-CLIE"
    creditObjectNumber = "CLI-330012"; creditObjectType = "CLIENTE"
    programName = "PGM-TELA-CLI"; errorCode = "ASRA"
    errorMessage = "Addressing exception na area de tela CICS"
    variables = @(@{ name = "WS-EIBCALEN"; value = "0" }, @{ name = "WS-COMMAREA"; value = "UNINIT" })
}

Write-Host ""
Write-Host ("-" * 72)

# --- RELATORIO FINAL ---
$total     = $Results.Count
$ok        = ($Results | Where-Object { $_.Success }).Count
$fail      = $total - $ok
$minMs     = ($Results | Measure-Object -Property Ms -Minimum).Minimum
$maxMs     = ($Results | Measure-Object -Property Ms -Maximum).Maximum
$avgMs     = ($Results | Measure-Object -Property Ms -Average).Average
$p95       = ($Results | Sort-Object Ms | Select-Object -Skip ([Math]::Floor($total * 0.95)) -First 1).Ms

Write-Host ""
Write-Host "  RELATORIO DE BENCHMARK"
Write-Host ("-" * 72)
Write-Host ("  Total de requisicoes : {0}" -f $total)
Write-Host ("  Sucesso / Falha      : {0} / {1}" -f $ok, $fail)
Write-Host ("  Tempo minimo         : {0:N2}ms" -f $minMs)
Write-Host ("  Tempo maximo         : {0:N2}ms" -f $maxMs)
Write-Host ("  Tempo medio          : {0:N2}ms" -f $avgMs)
if ($p95) { Write-Host ("  P95 (aprox)          : {0:N2}ms" -f $p95) }
Write-Host ("-" * 72)

Write-Host ""
Write-Host "  Top 3 requisicoes mais lentas:"
$Results | Sort-Object Ms -Descending | Select-Object -First 3 | ForEach-Object {
    Write-Host ("    {0,-18} {1,-20} {2:N2}ms" -f $_.Program, $_.Error, $_.Ms)
}

Write-Host ""
