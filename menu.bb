Global MainWindow = CreateWindow("Battle Empire " + version$, ClientWidth(Desktop()) / 2 - 300 / 2, ClientHeight(Desktop()) / 2 - 180 / 2, 300, 180, Desktop(), 1)

CreateLabel("Name:", 5, 8, 40, 15, MainWindow)
Global NameField = CreateTextField(45, 5, 100, 20, MainWindow)
CreateLabel("Team:",5, 33, 40, 15, MainWindow)
Global TeamBox = CreateComboBox(45, 30, 100, 20, MainWindow)

Global OptionsButton = CreateButton("Options", 190, 5, 100, 20, MainWindow)

CreateLabel("Tank:",150, 33, 40, 15, MainWindow)
Global SkinBox = CreateComboBox(190, 30, 100, 20, MainWindow)

CreateLabel("Servers:", 5, 55, 40, 15, MainWindow)
Global ServerList = CreateListBox(5, 70, 140, ClientHeight(MainWindow) - 75, MainWindow)
CreateLabel("Info:", 150, 55, 30, 15, MainWindow)
CreateLabel("Host:", 150, 70, 30, 15, MainWindow):Global HostLabel = CreateLabel("", 180, 70, 105, 15, MainWindow)
CreateLabel("Map:", 150, 85, 30, 15, MainWindow):Global MapLabel = CreateLabel("", 180, 85, 105, 15, MainWindow)
CreateLabel("Users:", 150, 100, 30, 15, MainWindow):Global UsersLabel = CreateLabel("", 180, 100, 105, 15, MainWindow)

Global RefreshButton = CreateButton("Refresh", 150, ClientHeight(MainWindow) - 25, 50, 20, MainWindow)
Global WebButton = CreateButton("Web", 205, ClientHeight(MainWindow) - 25, 40, 20, MainWindow)
Global JoinButton = CreateButton("Join", ClientWidth(MainWindow) - 45, ClientHeight(MainWindow) - 25, 40, 20, MainWindow)

SetGadgetIconStrip TeamBox, LoadIconStrip("data\graphics\teams.bmp")
	AddGadgetItem TeamBox, "Germany", 1, 0
	AddGadgetItem TeamBox, "Estonia", 0, 1
	
SetGadgetIconStrip SkinBox, LoadIconStrip("data\graphics\skins.bmp")
	AddGadgetItem SkinBox, "Light tank", 1, 0
	AddGadgetItem SkinBox, "Mine tank", 0, 1
	AddGadgetItem SkinBox, "Medium tank", 0, 2
	AddGadgetItem SkinBox, "Heavy tank", 0, 3
	AddGadgetItem SkinBox, "Constructer", 0, 4

SetGadgetText NameField, PlayerName$
SelectGadgetItem TeamBox, PlayerTeam
SelectGadgetItem SkinBox, PlayerSkin

If Instr(Lower(CommandLine$()), "/join")
	join$ = Mid$(CommandLine$(), Instr(CommandLine$(), "be://") + 5)
	If Right$(join$, 1) = "/" Then join$ = Left$(join$, Len(join$) - 1)
EndIf

serverliststream = SendServerListRequest()
Repeat
	If serverliststream
		If Eof(serverliststream)
			CloseTCPStream serverliststream
			serverliststream = False
			If Not CountGadgetItems(ServerList) Notify "No servers were found!"
		ElseIf ReadAvail(serverliststream)
			txt$ = ReadLine$(serverliststream)
			DebugLog(txt$);
			If Len(txt$) > 8
				While Instr(txt$, "\'")
					txt$ = Replace(txt$, "\'", "'")
				Wend
				While Instr(txt$, "\" + Chr(34))
					txt$ = Replace(txt$, "\" + Chr(34),  Chr(34))
				Wend
				
				If Instr(txt$, Chr(0))
					address$ = Left$(txt$, Instr(txt$, Chr(0)) - 1)
					txt$ = Right$(txt$, Len(txt$) - Len(address$) - 1)
					name$ = Left$(txt$, Instr(txt$, Chr(0)) - 1)
					txt$ = Right$(txt$, Len(txt$) - Len(name$) - 1)
					mapname$ = Left$(txt$, Instr(txt$, Chr(0)) - 1)
					txt$ = Right$(txt$, Len(txt$) - Len(mapname$) - 1)
					clients$ = Left$(txt$, Instr(txt$, Chr(0)) - 1)
					
					this.server = New server
						this\address = address$
						this\name = name$
						this\map = mapname$
						this\clients = clients$
					
					AddGadgetItem ServerList, this\name
					If address$ = join$
						SelectGadgetItem(ServerList, CountGadgetItems(ServerList) - 1)
						If ConnectToServer() Exit
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
	Select WaitEvent(50)
		Case $401
			Select EventSource()
				Case NameField
					If Len(TextFieldText(nameField)) > 12
						SetGadgetText nameField, Left$(TextFieldText(nameField), 12)
					EndIf
				Case ServerList
					i = 0
					For this.server = Each server
						If SelectedGadgetItem(ServerList) = i
							SetGadgetText HostLabel, this\address
							SetGadgetText mapLabel, this\map
							SetGadgetText UsersLabel, this\clients
							Exit
						EndIf
						i = i + 1
					Next
				Case OptionsButton
					OptionsWindow = CreateWindow("Options", ClientWidth(Desktop()) / 2 - 200 / 2, ClientHeight(Desktop()) / 2 - 200 / 2, 200, 200, Desktop(), 1)
					VSyncButton = CreateButton("Enable VSync", 5, 5, 90, 15, OptionsWindow, 2)
					FullScreenButton = CreateButton("Fullscreen", 100, 5, 90, 15, OptionsWindow, 2)
					TransparentExplosionButton = CreateButton("Transparent exsplosions", 5, 25, 150, 15, OptionsWindow, 2)
					ThreeDWallsButton = CreateButton("3D walls", 5, 45, 70, 15, OptionsWindow, 2)
					CreateLabel("Smoke delay:", 5, 68, 75, 15, OptionsWindow)
					SmokeDelayField = CreateTextField(80, 65, 50, 20, OptionsWindow)
					CreateLabel("millisecs *", 135, 68, 45, 15, OptionsWindow)
					CreateLabel("Msg buffer size:", 5, 93, 75, 15, OptionsWindow)
					MessageBufferSizeField = CreateTextField(80, 90, 50, 20, OptionsWindow)
					CreateLabel("lines", 135, 93, 45, 15, OptionsWindow)
					CreateLabel("Max particles:", 5, 118, 75, 15, OptionsWindow)
					MaxParticlesField = CreateTextField(80, 115, 50, 20, OptionsWindow)
					SaveButton = CreateButton("Save", ClientWidth(OptionsWindow) - 55, ClientHeight(OptionsWindow) - 25, 50, 20, OptionsWindow)
					
					CreateLabel("* - 0 to disable", 5, ClientHeight(OptionsWindow) - 22, 100, 15, OptionsWindow)
					
					SetButtonState VSyncButton, VSync
					SetButtonState FullscreenButton, ScreenMode
					SetButtonState TransparentExplosionButton, TransparentExplosion
					SetButtonState ThreeDWallsButton, ThreeDWalls
					SetGadgetText SmokeDelayField, SmokeDelay
					SetGadgetText MessageBufferSizeField, MessageBufferSize
					SetGadgetText MaxParticlesField, MaxParticles
				Case Refreshbutton
					serverliststream = SendServerListRequest()
				Case WebButton
					ExecFile("http://battleempire.kylm.com")
				Case JoinButton
					ChangeConfigValue("Name", TextFieldText(NameField))
					ChangeConfigValue("Team", SelectedGadgetItem(TeamBox))
					ChangeConfigValue("Tank", SelectedGadgetItem(SkinBox))
					
					If ConnectToServer() Exit
				Case SaveButton ; OPTION EVENTS -----------------------------------------------------------------------------------------------
					ChangeConfigValue("VSync", ButtonState(VSyncButton))
					ChangeConfigValue("Fullscreen", ButtonState(FullScreenButton))
					ChangeConfigValue("TransparentExplosion", ButtonState(TransparentExplosionButton))
					ChangeConfigValue("3DWalls", ButtonState(ThreeDWallsButton))
					ChangeConfigValue("SmokeDelay", TextFieldText(SmokeDelayField))
					ChangeConfigValue("MessageBufferSize", TextFieldText(MessageBufferSizeField))
					ChangeConfigValue("MaxParticles", TextFieldText(MaxParticlesField))
					
					VSync = ButtonState(VSyncButton)
					Screenmode = ButtonState(FullScreenButton)
					TransparentExplosion = ButtonState(TransparentExplosionButton)
					ThreeDWalls = ButtonState(ThreeDWallsButton)
					SmokeDelay = Int(TextFieldText(SmokeDelayField))
					MessageBufferSize = Int(TextFieldText(MessageBufferSizeField))
					MaxParticles = TextFieldText(MaxParticlesField)
					
					FreeGadget ActiveWindow()
			End Select
		Case $803
			Select ActiveWindow()
				Case MainWindow
					FreeGadget MainWindow
					End
				Default
					FreeGadget ActiveWindow()
			End Select
	End Select
Forever

Function ConnectToServer()
	If Len(TextFieldText(NameField)) > 1
		If SelectedGadgetItem(ServerList) => 0
			i = 0
			For this.server = Each server
				If SelectedGadgetItem(ServerList) = i
					ServerName$ = this\name$
					address$ = this\address
					Exit
				EndIf
				i = i + 1
			Next
			CurrentHost$ = Left$(address$, Instr(address$, ":") - 1)
			CurrentPort = Mid$(address$, Instr(address$, ":") + 1)
			
			Stream = CreateUDPStream()
			If Stream
				PlayerName$ = TextFieldText(NameField)
				PlayerTeam = SelectedGadgetItem(TeamBox)
				PlayerSkin = SelectedGadgetItem(SkinBox)
				
				Include "prepare.bb"
				
				Return Stream
			Else
				Notify "Unable to connect to " + host$ + ":" + port + " !" + Chr(13) + Chr(10) + "Please refresh the server list!"
			EndIf
		Else
			Notify "You need to select the server!"
		EndIf
	Else
		Notify "Name is too short!"
	EndIf
End Function

Function SendServerListRequest()
serverliststream = OpenTCPStream(web$, 80)
	If serverliststream
		WriteLine serverliststream, "GET " + webDir$ + "/servers/servers.php HTTP/1.1"
		WriteLine serverliststream, "Host: " + web$
		WriteLine serverliststream, "User-Agent: BattleEmpire " + version$
		WriteLine serverliststream, "Connection: Close"
		WriteLine serverliststream, ""
	EndIf
	tim = MilliSecs()
	While Abs(MilliSecs() - tim) < 2000
		If serverliststream
			If Not Eof(serverliststream)
				If ReadAvail(serverliststream)
					txt$ = ReadLine$(serverliststream)
					If txt$ = "" Exit
				EndIf
			EndIf
		EndIf
	Wend
	
	For this.server = Each server:Delete this:Next
	ClearGadgetItems ServerList
	
	Return serverliststream
End Function