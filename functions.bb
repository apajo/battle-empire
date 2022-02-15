Function AddMessage(txt$, col$)
	For i = 1 To MessageBufferSize - 1
		If (Not MessageBuffer(MessageBufferSize, 1) = "")
			MessageBuffer(i, 1) = MessageBuffer(i + 1, 1)
			MessageBuffer(i, 2) = MessageBuffer(i + 1, 2)
		EndIf
	Next
	
	MessageBuffer(MessageBufferSize, 1) = ""
	MessageBuffer(MessageBufferSize, 2) = ""
	
	For i = 1 To MessageBufferSize
		If MessageBuffer(i, 1) = ""
			MessageBuffer(i, 1) = txt$
			MessageBuffer(i, 2) = col$
			Exit
		EndIf
	Next
End Function

Function paintimage(image, frames, red, green, blue)
	For frame = 0 To frames - 1
		SetBuffer ImageBuffer(image, frame)
		For y = 0 To ImageHeight(image) - 1
			For x = 0 To ImageWidth(image) - 1
				GetColor x,y
				tempred = red * (Float ColorRed() / 255)
				tempgreen = green * (Float ColorGreen() / 255)
				tempblue = blue * (Float ColorBlue() / 255)			
				Color tempred, tempgreen, tempblue
				Plot x,y
			Next
		Next
	Next
	SetBuffer BackBuffer()
End Function

Function TextureImage(image, frames, texture, MaskRed, MaskGreen, MaskBlue)
	LockBuffer ImageBuffer(texture)
	For frame = 0 To frames - 1
		LockBuffer ImageBuffer(image, frame)
		sx = -ImageXHandle(image)
		sy = -ImageYHandle(image)
		For yy = 0 To ImageHeight(image) - 1
			For xx = 0 To ImageWidth(image) - 1
				imgcol = ReadPixelFast(xx - sx, yy - sy, ImageBuffer(image, frame))
				imgr = (imgcol Shr 16) And $FF
				imgg = (imgcol Shr 8) And $FF
				imgb = imgcol And $FF
				If Not imgr = MaskRed And imgg = MaskGreen And imgb = MaskBlue
					averangecolor# = Float ((imgr + imgg + imgb) / 3) / 255.0
					
					textcol = ReadPixelFast(xx - sx, yy - sy, ImageBuffer(texture))
					textr = (textcol Shr 16) And $FF
					textg = (textcol Shr 8) And $FF
					textb = textcol And $FF
					
					r = textr * averangecolor#
					g = textg * averangecolor#
					b = textb * averangecolor#
					
					argb = (b Or (g Shl 8) Or (r Shl 16) Or ($FF000000))
					WritePixelFast xx, yy, argb, ImageBuffer(image, frame)
				EndIf
			Next
		Next
		UnlockBuffer ImageBuffer(image, frame)
	Next
	UnlockBuffer ImageBuffer(texture)
End Function

Function DrawTransparentImage(image, x, y, frame=0, redmask=0, greenmask=0, bluemask=0, strength#=1.0)
	If x > GraphicsWidth() + ImageXHandle(image) Or y > GraphicsHeight() + ImageYHandle(image) Or x < -ImageXHandle(image) Or y < -ImageYHandle(image) Then Return
	LockBuffer GraphicsBuffer()
	LockBuffer ImageBuffer(image, frame)
	sx = -ImageXHandle(image)
	sy = -ImageYHandle(image)
	For yy = sy To sy + ImageHeight(image) - 1
		For xx = sx To sx + ImageWidth(image) - 1
			If x + xx => 0 And x + xx < GraphicsWidth() And y + yy=> 0 And y + yy < GraphicsHeight()
				imgcol = ReadPixelFast(xx - sx, yy - sy, ImageBuffer(image, frame))
				imgr = (imgcol Shr 16) And $FF
				imgg = (imgcol Shr 8) And $FF
				imgb = imgcol And $FF
				If (Not imgr = redmask) And (Not imgg = greenmask) And (Not imgb = bluemask)
					backcol = ReadPixelFast(x + xx, y + yy, GraphicsBuffer())
					backr = (backcol Shr 16) And $FF
					backg = (backcol Shr 8) And $FF
					backb = backcol And $FF
					
					r = backr + (imgr - backr) * (.5 + Float imgr / 255 * .5) * strength#
					g = backg + (imgg - backg) * (.5 + Float imgg / 255 * .5) * strength#
					b = backb + (imgb - backb) * (.5 + Float imgb / 255 * .5) * strength#
					
					argb = (b Or (g Shl 8) Or (r Shl 16) Or ($FF000000))
					WritePixelFast x + xx, y + yy, argb
				EndIf
			EndIf
		Next
	Next
	UnlockBuffer ImageBuffer(image, frame)
	UnlockBuffer GraphicsBuffer()
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

Function GetAngle(basex, basey, targetx, targety)
	If targety>basey-1 Then
		angle=ACos((targetx-basex)/Sqr((targetx-basex)*(targetx-basex)+(targety-basey)*(targety-basey)))
	Else
		If targetx>basex-1 Then
			angle=270+90-ACos((targetx-basex)/Sqr((targetx-basex)*(targetx-basex)+(targety-basey)*(targety-basey)))
		Else
			angle=360-ACos((targetx-basex)/Sqr((targetx-basex)*(targetx-basex)+(targety-basey)*(targety-basey)))
		EndIf
	EndIf

	If targetx=basex And targety=basey Then Return 0 Else Return angle
End Function

Function GetLenght(x1, y1, x2, y2)
	Return Sqr((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
End Function

Function CountFPS()
	If Not Rand(0, 10) Then fps = 1000.0 / (MilliSecs() - fpstime)
	fpstime = MilliSecs()
End Function

Function AppIcon(iconfile$)
	hwnd = api_GetActiveWindow()
	icon = api_LoadIcon(hwnd, iconfile$)
	api_SetClassLong hwnd, -14, icon
End Function

Function CreateProtocolRegistryFile(registryfile$, name$, protocol$, executable$)
	wf = WriteFile(registryfile$)
	If wf
		While Instr(executable$, "\", i)
			i = Instr(executable$, "\", i)
			executable$ = Mid$(executable$, 1, i) + "\" + Mid$(executable$, i + 1)
			i = i + 2
		Wend
		
		WriteLine wf, "Windows Registry Editor Version 5.00"
		WriteLine wf, ""
		WriteLine wf, "[HKEY_CLASSES_ROOT\" + protocol$ + "]"
		WriteLine wf, "@=" + Chr(34) + "URL:" + name$ + Chr(34)
		WriteLine wf, Chr(34) + "URL Protocol" + Chr(34) + "=" + Chr(34) + Chr(34)
		WriteLine wf, ""
		WriteLine wf, "[HKEY_CLASSES_ROOT\" + protocol$ + "\DefaultIcon]"
		WriteLine wf, "@=" + Chr(34) + "\" + Chr(34) + executable$ + "\" + Chr(34) + Chr(34)
		WriteLine wf, "[HKEY_CLASSES_ROOT\" + protocol$ + "\shell]"
		WriteLine wf, "[HKEY_CLASSES_ROOT\" + protocol$ + "\shell\open]"
		WriteLine wf, "[HKEY_CLASSES_ROOT\" + protocol$ + "\shell\open\command]"
		WriteLine wf, "@=" + Chr(34) + "\" + Chr(34) + executable$ + "\" + Chr(34) + " /join %1" + Chr(34)
		
		CloseFile wf
		Return True
	Else
		Return False
	EndIf
End Function

Function UnHex%(txt$)
	txt$ = Upper(txt$)
	
	Local a = Asc(Mid$(txt$, 1, 1)) - 48
	If a > 10 Then a = a - 7
	Local b = Asc(Mid$(txt$, 2, 1)) - 48
	If b > 10 Then b = b - 7
	
	Return a * 16 + b
End Function