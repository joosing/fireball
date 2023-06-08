$ftpSession = New-Object -TypeName System.Net.WebClient
$ftpSession.Credentials = New-Object System.Net.NetworkCredential("id", "password")
$executionTimes = @()
for ($i = 1; $i -le 50; $i++) {
    $result = Measure-Command {
        $ftpSession.DownloadFile("ftp://localhost/remote-1000.dat", "c:\Temp\remote-ftp-1000.dat")
    } | Select-Object -ExpandProperty TotalMilliseconds
	Start-Sleep -Milliseconds 100
	Write-Host "$i execution time: $result ms"
    $executionTimes += $result
}
$averageTime = ($executionTimes | Measure-Object -Average).Average
Write-Host "Average execution time: $averageTime ms"
$ftpSession.Dispose()
