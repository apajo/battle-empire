SeedRnd MilliSecs()

;Types
Type map
	Field tile
	Field x
	Field y
End Type
;End of types

;Variables
Global Version$ = "1.0"
Global w = 600
Global h = 500

Global SVH
Global SVV
Global CW
Global CH

Global Canvas
Global MapWidth
Global MapHeight

Global NewMapWindow
Global WidthField
Global HeightField
Global NewMapButton

Global EditTile

Global TileWidth = 32
Global cmndline$ = CommandLine$()
Global Saved = True

Global FrameTimer = CreateTimer(8)
;End of variables

Global MainWindow = CreateWindow("Map editor " + Version$, ClientWidth(Desktop()) / 2 - w / 2, ClientHeight(Desktop()) / 2 - h / 2, w, h, Desktop(), 1 + 2)
AppTitle "Editor " + Version$
AppIcon("editor.exe")
SetMinWindowSize MainWindow, 400, 300

;Images
image = LoadImage("data\graphics\environment.bmp")
Global TileCount = ImageWidth(image) / ImageHeight(image)
FreeImage image

Global tiles = LoadAnimImage("data\graphics\environment.bmp", TileWidth, TileWidth, 0, TileCount, 2)
Global Strip = LoadIconStrip("data\graphics\environment.bmp")

MaskImage tiles, 255, 0, 255
;End of images

Include "editorgadgets.bb"

If (Not cmndline$ = "") And (Not cmndline$ = " ")
	If Confirm("Are you sure you want to open '" + cmndline$ + "'?")
		If Left$(cmndline$, 1) = " " Then cmndline$ = Right$(cmndline$, Len(cmndline$) - 1)
		LoadMap(cmndline$)
	EndIf
EndIf

Repeat
	If TimerTicks(FrameTimer) => 1
		SVH = SliderValue(HScroll)
		SVV = SliderValue(VScroll)
		CW = ClientWidth(MainPanel)
		CH = ClientHeight(MainPanel)

		DrawMap()
		DrawGrids()

		If canvas
			FlipCanvas Canvas
			Cls
		EndIf

		ResetTimer FrameTimer
	Else
		Events()
		
		If NewMapWindow
			If Not ActiveWindow() = NewMapWindow
				api_BringWindowToTop(QueryObject(NewMapWindow, 1))
			EndIf
		EndIf
	EndIf
Forever

Function Events()
	Select WaitEvent(100)
		Case $201
			Select EventData()
				Case 1
					Select EventSource()
						Case Canvas
							EditTile = True
					End Select
				Case 2
					Select EventSource()
						Case Canvas
							x = MouseX(Canvas)
							y = MouseY(Canvas)
							ChooseTileFromMap((x + SVH) / TileWidth, (y + SVV) / TileWidth)
					End Select
			End Select
		Case $401
			Select EventSource()
				Case NewMapButton
					RemoveMap()
					MapWidth = TextFieldText(WidthField)
					MapHeight = TextFieldText(HeightField)
					CreateNewMap(SelectedGadgetItem(TileBox))
					FreeGadget NewMapWindow
					NewMapWindow = 0
				Case Toolbar
					Select EventData()
						Case 0
							CreateNewMapWindow()
						Case 1
							file$ = RequestFile("Choose the map to open...", "bem")
							If Not file$ = ""
								LoadMap(file$)
							EndIf
						Case 2
							If canvas
								file$ = RequestFile("Choose the file to save the map...", "bem", 1)
								If Not file$ = ""
									SaveMap(file$)
								EndIf
							EndIf
						Case 3
							Notify "Map editor " + Version$ + " by anC"
						Case 4
							If FileType("NemEd.txt") = 1
								ExecFile("notepad editor.txt")
							Else
								Notify "Unable to find 'editor.txt'!", 1
							EndIf
						Case 5
							TheEnd()
					End Select
			End Select
		Case $802
			SetSliderRange VScroll, ClientHeight(MainPanel), MapHeight * TileWidth
			SetSliderRange HScroll, ClientWidth(MainPanel), MapWidth * TileWidth
		Case $803
			Select ActiveWindow()
				Case NewMapWindow
					FreeGadget NewMapWindow
					NewMapWindow = 0
				Case MainWindow
					TheEnd()
				Default
					FreeGadget ActiveWindow()
			End Select
	End Select
	
	If MouseDown(1) And EditTile
		x = MouseX(Canvas)
		y = MouseY(Canvas)
		ChangeTile((x + SVH) / TileWidth, (y + SVV) / TileWidth, SelectedGadgetItem(TileBox))
	Else
		EditTile = 0
	EndIf
	
End Function

Function CreateNewMapWindow()
	If Not NewMapWindow
		NewMapWindow = CreateWindow("New map", MouseX(), MouseY(), 165, 105, Desktop(), 1 + 16)
		CreateLabel("Width:", 5, 8, 40, 15, NewMapWindow)
		WidthField = CreateTextField(50, 5, 100, 20, NewMapWindow)
		CreateLabel("Height:", 5, 33, 40, 15, NewMapWindow)
		HeightField = CreateTextField(50, 30, 100, 20, NewMapWindow)
		NewMapButton = CreateButton("Create", 50, 55, 100, 20, NewMapWindow)
		
		SetGadgetText WidthField, "30"
		SetGadgetText HeightField, "30"
	EndIf
End Function

Function CreateNewMap(tile)
	If MapWidth < 20
		Notify "Map width can't be less than 20!"
		MapWidth = 20
	EndIf
	If MapHeight < 15
		Notify "Map height can't be less than 15!"
		MapHeight = 15
	EndIf
	If MapWidth > 300
		Notify "Map width can't be greater than 300!"
		MapWidth = 300
	EndIf
	If MapHeight > 300
		Notify "Map height can't be greater than 300!"
		MapHeight = 300
	EndIf
	
	If Canvas FreeGadget Canvas
	Canvas = CreateCanvas(0, 0, ClientWidth(Desktop()) - 150, ClientHeight(Desktop()) - 50, MainPanel)
	SetGadgetLayout Canvas, 1, 0, 1, 0
	SetBuffer CanvasBuffer(Canvas)
	SetFont LoadFont("System", 18)

	If tile => 0
		For y = 0 To MapHeight - 1
			For x = 0 To MapWidth - 1
				this.map = New map
					this\tile = tile
					this\x = x
					this\y = y
			Next
		Next
	EndIf
	
	EnableGadget VScroll
	EnableGadget HScroll
	
	SetSliderValue VScroll, 0
	SetSliderValue HScroll, 0
	SetSliderRange VScroll, ClientHeight(MainPanel), MapHeight * TileWidth
	SetSliderRange HScroll, ClientWidth(MainPanel), MapWidth * TileWidth
End Function

Function RemoveMap()
	For this.map = Each map
		Delete this
	Next
	MapWidth = 0
	MapHeight = 0
End Function

Function LoadMap(file$)
	If Not FileType(file$) = 1
		Notify "Can't open '" + file$ + "'!"
		Return
	EndIf
	RemoveMap()
	
	RF = ReadFile(file$)
	If RF
		Saved = True
		SetGadgetText MapNameField, ReadString$(RF)
		MapWidth = ReadShort(RF)
		MapHeight = ReadShort(RF)
		
		For y = 0 To MapHeight - 1
			For x = 0 To MapWidth - 1
				this.map = New map
					this\tile = ReadByte(RF)
					this\x = x
					this\y = y
			Next
		Next
		CloseFile RF
		
		CreateNewMap(-1)
	Else
		Notify "Unable to load the map!"
	EndIf
End Function

Function SaveMap(file$)
	WF = WriteFile(file$)
	If WF
		WriteString WF, TextFieldText(MapNameField)
		
		WriteShort WF, MapWidth
		WriteShort WF, MapHeight
		For this.map = Each map
			WriteByte WF, this\tile
		Next
		CloseFile WF
		Saved = True
	Else
		Notify "Unable to save the map!"
	EndIf
End Function

Function DrawGrids()
	If ButtonState(ShowGrid) Then If Canvas
		Color 200, 200, 200
		
		;Horizontal
		For i = SVH To SVH + CW
			If Not (i Mod TileWidth)
				Rect i - SVH, 0, 1, CH
			EndIf
		Next
		;Vertical
		For i = SVV To SVV + CH
			If Not (i Mod TileWidth)
				Rect 0, i - SVV, CW, 1
			EndIf
		Next
		
	EndIf
End Function

Function DrawMap()
	If Canvas
		Color 255, 0, 0
		For this.map = Each map
			If (this\x * TileWidth) > SVH - TileWidth And (this\x * TileWidth) < SVH + CW
				If (this\y * TileWidth) > SVV - TileWidth And (this\y * TileWidth) < SVV + CH
					DrawBlock tiles, this\x * TileWidth - SVH, this\y * TileWidth - SVV, this\tile
				EndIf
			EndIf
		Next
	EndIf
End Function

Function ChangeTile(x, y, tile)
	Saved = False
	
	For this.map = Each map
		If this\x = x And this\y = y
			this\tile = tile
		EndIf
	Next
End Function

Function ChooseTileFromMap(x, y)
	For this.map = Each map
		If this\x = x And this\y = y
			SelectGadgetItem TileBox, this\tile
		EndIf
	Next
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
	EndIf
	Return False
End Function

Function AppIcon(iconfile$)
	hwnd = User32_GetActiveWindow()
	icon = LoadIcon(hwnd, iconfile$, 0)
	User32_SetClassLong hwnd, -14, icon
End Function

Function TheEnd()
	If Not Saved Then If Confirm("The map is unsaved! Quit anyway?")
		FreeGadget MainWindow
	Else
		Return
	EndIf
	End
End Function