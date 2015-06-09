@ECHO OFF 

:: Clears IE cookies,History,cache and passwords
RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 255 (Deletes ALL History)
RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 1 (Deletes History Only)
RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 2 (Deletes Cookies Only)
RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 8 (Deletes Temporary Internet Files Only)
RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 16 (Deletes Form Data Only)
RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 32 (Deletes Password History Only)

