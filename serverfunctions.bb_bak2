Function AddMessage(msg$)
	If Not TextAreaLen(TextArea)
		AddTextAreaText TextArea, msg$
		SetTextAreaText TextArea, Right$(TextAreaText(TextArea), 5 * 1024)
	Else
		AddTextAreaText TextArea, Chr(13) + Chr(10) + msg$
		SetTextAreaText TextArea, Right$(TextAreaText(TextArea), 5 * 1024)
	EndIf
End Function

Function UpdateStats()
	If TimerTicks(statstimer) => 100
		truetime# = Float 100.0 / TimerTicks(statstimer)
		
		clients = 0
		For this.connection = Each connection
			clients = clients + 1
		Next
		
		SetGadgetText onlinelabel, ConvertTime(TimerTicks(onlinetime))
		SetGadgetText clientslabel, clients
		SetGadgetText maplabel, MapName$
		SetGadgetText uploadlabel, ConvertBytes(totalsent) + " (" + ConvertBytes((totalsent - lastsent) * truetime#) + "/s)"
		SetGadgetText downloadlabel, ConvertBytes(totalreceived) + " (" + ConvertBytes((totalreceived - lastreceived) * truetime#) + "/s)"
		
		tooltip$ = GadgetText$(MainWindow) + Chr(13) + Chr(10)
		tooltip$ = tooltip$ + "Clients: " + clients  + Chr(13) + Chr(10)
		tooltip$ = tooltip$ + "Transfer: " + ConvertBytes(totalsent + totalreceived) + " (" + ConvertBytes((totalsent - lastsent + totalreceived - lastreceived) * truetime#) + "/s)"
		
		ggTraySetToolTip(tooltip$)
		
		lastsent = totalsent
		lastreceived = totalreceived
		
		ResetTimer statstimer
	EndIf
End Function

Function ConfigValue$(variable$)
	rf = ReadFile("config.cfg")
	If rf
		While Not Eof(rf)
			txt$ = ReadLine$(rf)
			If Left$(Lower(txt$), Instr(txt$, "=")) = Lower(variable$ + "=")
				Return Right$(txt$, Len(txt$) - Instr(txt$, "="))
			EndIf
		Wend
		
		CloseFile rf
	EndIf
	Return False
End Function

Function DefaultConfigValue$(variable$)
	rf = ReadFile("data\default.cfg")
	If rf
		While Not Eof(rf)
			txt$ = ReadLine$(rf)
			If Left$(Lower(txt$), Instr(txt$, "=")) = Lower(variable$ + "=")
				Return Right$(txt$, Len(txt$) - Instr(txt$, "="))
			EndIf
		Wend
		CloseFile rf
	EndIf
	Return False
End Function

Function ChangeConfigValue(variable$, value$)
	rf = ReadFile("config.cfg")
	If rf
		For this.file = Each file
			Delete this
		Next
		While Not Eof(rf)
			this.file = New file
				this\txt$ = ReadLine$(rf)
		Wend
		CloseFile rf
	EndIf
	
	wf = WriteFile("config.cfg")
	If wf
		For this.file = Each file
			If Left$(Lower(this\txt$), Instr(this\txt$, "=")) = Lower(variable$ + "=")
				WriteLine wf, Left$(this\txt$, Instr(this\txt$, "=")) + value$
			Else
				WriteLine wf, this\txt$
			EndIf
		Next
		CloseFile wf
	EndIf
End Function

Function RandomBotName$()
	Local file = ReadFile("data\bots.txt")
	Local i%, j%, name$, botname$
	If file
		While Not Eof(file)
			name$ = ReadLine$(file)
			If Len(name$) => 3 Then i = i + 1
		Wend
		If i > 0
			SeekFile(file, 0)
			j = Rand(1, i)
			For a = 1 To j - 1
				ReadLine$(file)
			Next
			name$ = ReadLine$(file)
			botname$ = name$
		Else
			name$ = "Bot"
			botname$ = name$
		EndIf
		Repeat
			occ = False
			For this.connection = Each connection
				If this\name$ = botname$ Then occ = True
			Next
			If occ Then botname$ = name$ + Rand(100, 999)
		Until occ = False
		CloseFile file
		Return botname$
	EndIf
End Function

Function Sector$(txt$, separator$, sector%)
	Local result$ = "", occ
	For i = 1 To Len(txt$)
		If Mid$(txt$, i, 1) = separator$
			occ = occ + 1
		Else
			If occ = sector Then result$ = result$ + Mid$(txt$, i, 1)
		EndIf
		If occ > sector Then Exit
	Next
	Return result$
End Function

Function CountFPS()
	If Not Rand(0, 10) Then fps = 1000.0 / (MilliSecs() - fpstime)
	fpstime = MilliSecs()
End Function

Function ResetGadgets()
	OptionsWindow = 0
	GameSpeedField = 0
	SpeedField = 0
	TurnSpeedField = 0
	BulletSpeedField = 0
	BulletDelayField = 0
	RespawnDelayField = 0
	RealisticModeButton = 0
	FriendlyFireButton = 0
	GameTypeBox = 0
	MovementStyleBox = 0
	AdminPasswordField = 0
	ApplyButton = 0
	DefaultButton = 0
	
	MapList = 0
	LoadMapButton = 0
End Function

Function NotifyJoiner(name$)
	Local count = 0
	Local img = LoadImage("data\graphics\joiner.bmp")
	If img
		For this.joinerwindow = Each joinerwindow:count = count + 1:Next
		
		this.joinerwindow = New joinerwindow
			this\window = CreateWindow("Joiner notification", ClientWidth(Desktop()) - ImageWidth(img), ClientHeight(Desktop()) - 30 - ImageHeight(img) * (count + 1), ImageWidth(img), ImageHeight(img), Desktop(), 16)
			this\time = MilliSecs()
			api_SetWindowPos(QueryObject(this\window, 1), -1, 0, 0, 0, 0, $0003)
			canvas = CreateCanvas(0, 0, ClientWidth(this\window), ClientHeight(this\window), this\window)
			SkinWindow(this\window, img, 255, 0, 255)
			CopyRect 0, 0, ImageWidth(img), ImageHeight(img), 0, 0, ImageBuffer(img), CanvasBuffer(canvas)
			SetBuffer CanvasBuffer(canvas)
			SetFont LoadFont("System", 18)
			Color 0, 0, 0
			Text 10, 8, "Joined:"
			Text 10, 23, name$
			FlipCanvas Canvas
		
		FreeImage img
	EndIf
End Function

Function SkinWindow(window, img, r, g, b)
	w = ImageWidth(img)
	h = ImageHeight(img)
	mask = (255 Shl 24) + (r Shl 16) + (g Shl 8) + b
	hWnd = QueryObject(window, 1)
	hRgn = api_CreateRectRgn(0, 0, w, h)
	
	LockBuffer ImageBuffer(img)
	For y = 0 To h - 1
		For x = 0 To w - 1
			pixel = ReadPixelFast(x, y, ImageBuffer(img))
			If pixel = mask 
				hTempRgn = api_CreateRectRgn(x, y, x + 1, y + 1)
				api_CombineRgn(hRgn, hRgn, hTempRgn, 3)
				DeleteObject(hTempRgn)
			EndIf
		Next
	Next
	UnlockBuffer ImageBuffer(img)
	
	api_SetWindowRgn(hWnd, hRgn, True)
	DeleteObject(hRgn)
End Function