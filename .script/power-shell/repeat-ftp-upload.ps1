$ftpSession = New-Object -TypeName System.Net.WebClient
$ftpSession.Credentials = New-Object System.Net.NetworkCredential({id}, {password})
$executionTimes = @()
for ($i = 1; $i -le 10; $i++) {
    $result = Measure-Command {
        $ftpSession.UploadFile("ftp://localhost/remote-ftp.dat", "c:/Temp/local.dat")
    } | Select-Object -ExpandProperty TotalMilliseconds
	Start-Sleep -Milliseconds 100
    $executionTimes += $result
}
$averageTime = ($executionTimes | Measure-Object -Average).Average
Write-Host "Average execution time: $averageTime ms"
$ftpSession.Dispose()
