$execTimeList = @()

for ($i = 1; $i -le 10; $i++) {
    $execTime = Measure-Command {
        dir
    } | Select-Object -ExpandProperty TotalMilliseconds

    $execTimeList += $execTime
}

$averageTime = ($execTimeList | Measure-Object -Average).Average
Write-Host "Curl + Nginx, Average Time : $averageTime ms"