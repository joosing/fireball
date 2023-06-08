$executionTimes = @()
for ($i = 1; $i -le 50; $i++) {
    $result = Measure-Command {
        curl.exe -s -o c:/files/local-curl-1000.dat http://localhost/files/remote-1000.dat
    } | Select-Object -ExpandProperty TotalMilliseconds
	Start-Sleep -Milliseconds 100
	Write-Host "$i execution time: $result ms"
    $executionTimes += $result
}
$averageTime = ($executionTimes | Measure-Object -Average).Average
Write-Host "Average execution time: $averageTime ms"
