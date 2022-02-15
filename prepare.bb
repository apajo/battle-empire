;Error check
WriteLine Stream, version$
SendUDPMsg Stream, IntIP(CurrentHost$), CurrentPort

tim = MilliSecs()
While Not RecvUDPMsg(Stream)
	Delay 500
	If Abs(MilliSecs() - tim) > 3000 Exit
Wend

error$ = ReadString$(Stream)
If Not error$ = ""
	Notify error$
	CloseUDPStream Stream
	Stream = False
Else
	PlayMusic("data\sounds\theme.mp3")
	
	If Not LoadMap()
		CloseUDPStream Stream
		Stream = False
		Notify "Connection failed!"
	EndIf
EndIf