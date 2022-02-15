SeedRnd MilliSecs()

;Types
Type Connection
	Field intip%
	Field id$
	Field port%
	Field map
	Field mapstream
	Field connected
	Field StreamBuffer$
	Field AdminState
	Field MessageTime
	
	Field uptodate
	Field alive
	Field name$
	Field team
	Field frags%
	Field deaths%
	Field deathtime%
	Field x#
	Field y#
	Field tx#
	Field ty#
	Field distance#
	Field angle
	Field skin
	Field ping$
	Field RespawnTime
	
	Field ai%
	Field aitime%
	Field airotate#
	Field aideathtime%
	Field aishoottime%
	Field enemyspottime%
	
	Field invisibletime%
	Field immortalitytime%
	Field rambotime%
End Type

Type bullet
	Field id$
	Field timid%
	Field x#
	Field y#
	Field angle#
	Field time%
End Type

Type mine
	Field id$
	Field timid%
	Field x#
	Field y#
	Field time%
End Type

Type map
	Field tile
	Field x%
	Field y%
End Type

Type constructionmap
	Field tile
	Field hp%
	Field x%
	Field y%
End Type

Type bonus
	Field id%
	Field typ%
	Field x%, y%
End Type

Type maps
	Field file$
End Type

Type file
	Field txt$
End Type

Type joinerwindow
	Field window
	Field time
End Type

Type spawnpoint
	Field team%
	Field x%, y%
End Type

Dim Map(300, 300)
;End of types

Include "serverruntime.bb"
Include "ProtocolCommands.bb"
Include "AI.bb"

;Variables
Const version$ = "1.08 resurrected"
Global infotimer = CreateTimer(4)
Global MapTimer = CreateTimer(1.0 / 60.0)
Global BonusTimer = CreateTimer(1.0 / 5)
Global mapbank = CreateBank(256)
Global Stream
Global clients
Global CurrentMap$
Global RuntimeMap$ = "runtime" + Rand(1000, 9999) + ".bem"
Global multiplier#
Global PackageFrequency = 4
Global StreamBank = CreateBank(128)
Global TimeoutDelay = 32000
Global JoinerWindowDelay = 8000

Global fps%
Global fpstime%

Global hostserver$ = "battleempire.pri.ee"
Global host$ = "battleempire.pri.ee"
Global updater$ = "/servers/update.php"
Global useragent$ = "BattleEmpire " + version$

Global web$ = ConfigValue("web")
Global webDir$ = Mid$(web$, Instr(web$, "/"))
web$ = Mid$(web$, 0, Instr(web$, "/"))
hostserver$ = web$
host$ = web$
updater$ = webDir$ + updater$

Global totalsent%
Global totalreceived%
Global lastsent%
Global lastreceived%

Global MapName$
Global MapWidth%
Global MapHeight%
Global TileSize% = 32

Global RedScore
Global BlueScore

Const BrickWallIndex = 100
Const SteelWallIndex = 101
Const BuildWallIndex = 107
Const BushIndex = 102
Const RedEagle = 105
Const BlueEagle = 106
Const RedSpawn = 103
Const BlueSpawn = 104

Global GameSpeed = ConfigValue("GameSpeed")
Global ViewDistance = ConfigValue("ViewDistance")
Global Speed# = ConfigValue("Speed")
Global Turn# = ConfigValue("TurnSpeed")
Global BulletSpeed# = ConfigValue("BulletSpeed")
Global BulletDelay = ConfigValue("BulletDelay")
Global RespawnDelay = ConfigValue("RespawnDelay")
Global RealisticMode = ConfigValue("RealisticMode")
Global FriendlyFire = ConfigValue("FriendlyFire")
Global AdminPassword$ = ConfigValue("AdminPassword")
Global MineLifeTime = ConfigValue("MineLifeTime")
Global GameType = ConfigValue("GameType")
Global MovementStyle = ConfigValue("MovementStyle")
Global FragLimit = ConfigValue("FragLimit")
Global TimeLimit = ConfigValue("TimeLimit")
Global Topic$ = ConfigValue("Topic")

Global OptionsWindow
Global GameSpeedField
Global SpeedField
Global TurnSpeedField
Global BulletSpeedField
Global BulletDelayField
Global RespawnDelayField
Global TopicField
Global RealisticModeButton
Global FriendlyFireButton
Global GameTypeBox
Global MovementStyleBox
Global AdminPasswordField
Global FragLimitField
Global TimeLimitField
Global ApplyButton
Global DefaultButton

Global MapList
Global LoadMapButton
;End of variables

;Window
Global MainWindow = CreateWindow("Battle Empire Server " + version$, ClientWidth(Desktop()) - 400, ClientHeight(Desktop()) - 390, 400, 360, Desktop(), 16 + 1)
AppTitle GadgetText$(MainWindow)
api_SetWindowPos(QueryObject(MainWindow, 1), -1, 0, 0, 0, 0, $0003)
Include "serverfunctions.bb"
;End of window

;Systemtray
ggTrayCreate(QueryObject(MainWindow, 1))
ggTraySetIconFromFile("data\server.ico")
ggTraySetToolTip(GadgetText$(MainWindow))
ggTrayShowIcon()
;End of systemtray

;Gadgets
Global HideButton = CreateButton("-", ClientWidth(MainWindow) - 15, 5, 10, 10, MainWindow)

CreateLabel("Address: ", 5, 8, 65, 15, MainWindow)
Global ipfield = CreateTextField(70, 5, 100, 20, MainWindow)
CreateLabel(":", 173, 8, 5, 15, MainWindow)
Global portfield = CreateTextField(180, 5, 40, 20, MainWindow)
CreateLabel("Name:", 5, 29, 65, 15, MainWindow)
Global namefield = CreateTextField(70, 26, 150, 20, MainWindow)
CreateLabel("Max clients:", 5, 50, 60, 15, MainWindow)
Global maxclientsfield = CreateTextField(70, 47, 20, 20, MainWindow)

CreateLabel("Online:", 230, 5, 50, 15, MainWindow)
Global onlinelabel = CreateLabel("", 285, 5, 90, 15, MainWindow)
CreateLabel("Clients:", 230, 20, 50, 15, MainWindow)
Global clientslabel = CreateLabel("", 285, 20, 90, 15, MainWindow)
CreateLabel("Map:", 230, 35, 50, 15, MainWindow)
Global maplabel = CreateLabel("", 285, 35, 90, 15, MainWindow)
CreateLabel("Upload:", 230, 50, 50, 15, MainWindow)
Global uploadlabel = CreateLabel("", 285, 50, 90, 15, MainWindow)
CreateLabel("Download:", 230, 65, 50, 15, MainWindow)
Global downloadlabel = CreateLabel("", 285, 65, 90, 15, MainWindow)

Global PlayersButton = CreateButton("Players", 160, 49, 60, 18, MainWindow)
Global OptionsButton = CreateButton("Options", 5, 70, 50, 18, MainWindow)
Global mapbutton = CreateButton("Choose map", 60, 70, 70, 18, MainWindow)
Global serverbutton = CreateButton("Create Server", 135, 70, 85, 18, MainWindow)

Global TextArea = CreateTextArea(5, 90, ClientWidth(MainWindow) - 10, ClientHeight(MainWindow) - 120, MainWindow, 1)
Global TextField = CreateTextField(5, ClientHeight(MainWindow) - 25, ClientWidth(MainWindow) - 65, 20, MainWindow)
Global SendButton = CreateButton("Send", ClientWidth(MainWindow) - 55, ClientHeight(MainWindow) - 25, 50, 20, MainWindow, 4)

SetTextAreaFont TextArea, LoadFont("Arial", 16, 1)
SetTextAreaTabs TextArea, 3
SetTextAreaColor TextArea, 0, 0, 0, 1
SetTextAreaColor TextArea, 200, 200, 200, 0

If ConfigValue("ServerAddress") = "" Then SetGadgetText ipfield, DottedIP(HostIP(CountHostIPs(""))) Else SetGadgetText ipfield, ConfigValue("ServerAddress")
SetGadgetText portfield, ConfigValue("ServerPort")
SetGadgetText namefield, ConfigValue("ServerName")
SetGadgetText maxclientsfield, ConfigValue("ServerMaxClients")

ActivateGadget TextField
;End of gadgets

Global onlinetime = CreateTimer(1):PauseTimer onlinetime
Global updatetimer = CreateTimer(1):PauseTimer updatetimer
Global statstimer = CreateTimer(100)

Repeat
	multiplier# = Float (MilliSecs() - last_milli) / (1000 / GameSpeed)
	last_milli = MilliSecs()
	
	Events(5)
	UpdateStats()
	If Stream
		ReadStream()
		MovePlayers()
		UpdateAI()
		MoveBullets()
		CheckForDeath()
		CheckTimeouts()
		CheckLimits()
		Bonus()
		
		If TimerTicks(updatetimer) => 2 * 60 Then UpdateServerInfo()
		CountFPS()
	EndIf
Forever

Function Events(wait)
	Select WaitEvent(wait)
		Case $401
			Select EventSource()
				Case PortField, NameField, MaxClientsField
					If Len(TextFieldText(portfield)) > 5 SetGadgetText portfield, Left$(TextFieldText(portfield), 5)
					If Len(TextFieldText(namefield)) > 32 SetGadgetText namefield, Left$(TextFieldText(namefield), 32)
					If Len(TextFieldText(MaxClientsField)) > 2 SetGadgetText MaxClientsField, Left$(TextFieldText(MaxClientsField), 2)
				Case HideButton
					api_ShowWindow(QueryObject(MainWindow, 1), 0)
				Case ExitButton
					TheEnd()
				Case TextField
					If EventData() = 13
						Message$(TextFieldText(textfield))
						SetGadgetText textfield, ""
					EndIf
				Case PlayersButton
					PlayersWindow = CreateWindow("Players", ClientWidth(Desktop()) / 2 - 210 / 2, ClientHeight(Desktop()) / 2 - (60 + clients * 15) / 2, 210, 60 + clients * 15, Desktop(), 1)
					CreateLabel("Name:", 5, 5, 30, 15, PlayersWindow)
					CreateLabel("Frags:", 90, 5, 30, 15, PlayersWindow)
					CreateLabel("Team:", 130, 5, 30, 15, PlayersWindow)
					CreateLabel("Ping:", 170, 5, 30, 15, PlayersWindow)
					
					For this.connection = Each connection
						CreateLabel(this\name$, 5, 25 + i * 15, 80, 15, PlayersWindow)
						CreateLabel(this\frags%, 90, 25 + i * 15, 35, 15, PlayersWindow)
						If this\team = 0 Then team$ = "GER" Else team$ = "EST"
						CreateLabel(team$, 130, 25 + i * 15, 35, 15, PlayersWindow)
						CreateLabel(this\ping, 170, 25 + i * 15, 35, 15, PlayersWindow)
						i = i + 1
					Next
				Case OptionsButton
					OptionsWindow = CreateWindow("Options", ClientWidth(Desktop()) / 2 - 250 / 2, ClientHeight(Desktop()) / 2 - 255 / 2, 250, 255, Desktop(), 1)
					CreateLabel("Game speed:", 5, 8, 65, 15, OptionsWindow)
					GameSpeedField = CreateTextField(70, 5, 60, 20, OptionsWindow)
					CreateLabel("Move speed:", 5, 33, 65, 15, OptionsWindow)
					SpeedField = CreateTextField(70, 30, 60, 20, OptionsWindow)
					CreateLabel("Turn speed:", 5, 58, 65, 15, OptionsWindow)
					TurnSpeedField = CreateTextField(70, 55, 60, 20, OptionsWindow)
					CreateLabel("Bullet speed:", 5, 83, 65, 15, OptionsWindow)
					BulletSpeedField = CreateTextField(70, 80, 60, 20, OptionsWindow)
					CreateLabel("Bullet delay:", 5, 108, 65, 15, OptionsWindow)
					BulletDelayField = CreateTextField(70, 105, 60, 20, OptionsWindow)
					CreateLabel("Spawn delay:", 5, 133, 65, 15, OptionsWindow)
					RespawnDelayField = CreateTextField(70, 130, 60, 20, OptionsWindow)
					CreateLabel("Topic:", 5, 158, 35, 15, OptionsWindow)
					TopicField = CreateTextField(40, 155, 90, 20, OptionsWindow)
					
					RealisticModeButton = CreateButton("Realistic mode", 5, 180, 120, 15, OptionsWindow, 2)
					FriendlyFireButton = CreateButton("Friendly fire", 5, 200, 120, 15, OptionsWindow, 2)
					
					CreateLabel("Game type:", 140, 5, 90, 15, OptionsWindow)
					GameTypeBox = CreateComboBox(140, 20, 100, 20, OptionsWindow)
					AddGadgetItem GameTypeBox, "Invasion"
					AddGadgetItem GameTypeBox, "Team DM"
					AddGadgetItem GameTypeBox, "Deathmatch"
					CreateLabel("Movement:", 140, 45, 90, 15, OptionsWindow)
					MovementStyleBox = CreateComboBox(140, 60, 100, 20, OptionsWindow)
					AddGadgetItem MovementStyleBox, "4 directional"
					AddGadgetItem MovementStyleBox, "36 directional"
					CreateLabel("Admin password:", 140, 85, 90, 15, OptionsWindow)
					AdminPasswordField = CreateTextField(140, 100, 90, 20, OptionsWindow)
					CreateLabel("Frag limit:", 140, 122, 90, 13, OptionsWindow)
					FragLimitField = CreateTextField(140, 135, 90, 20, OptionsWindow)
					CreateLabel("Time limit:", 140, 157, 90, 13, OptionsWindow)
					TimeLimitField = CreateTextField(140, 170, 90, 20, OptionsWindow)
					
					ApplyButton = CreateButton("Apply", ClientWidth(OptionsWindow) - 55, ClientHeight(OptionsWindow) - 25, 50, 20, OptionsWindow)
					DefaultButton = CreateButton("Defaults", ClientWidth(OptionsWindow) - 110, ClientHeight(OptionsWindow) - 25, 50, 20, OptionsWindow)
					
					SetGadgetText(GameSpeedField, GameSpeed)
					SetGadgetText(SpeedField, Speed)
					SetGadgetText(TurnSpeedField, Turn)
					SetGadgetText(BulletSpeedField, BulletSpeed)
					SetGadgetText(BulletDelayField, BulletDelay)
					SetGadgetText(RespawnDelayField,RespawnDelay)
					SetGadgetText(TopicField, Topic)
					SetButtonState(RealisticModeButton, RealisticMode)
					SetButtonState(FriendlyFireButton, FriendlyFire)
					SelectGadgetItem GameTypeBox, GameType
					SelectGadgetItem MovementStyleBox, MovementStyle
					SetGadgetText AdminPasswordField, AdminPassword$
					SetGadgetText FragLimitField, FragLimit
					SetGadgetText TimeLimitField, TimeLimit
				Case ApplyButton
					GameSpeed = Int(TextFieldText(GameSpeedField)):ChangeConfigValue("GameSpeed", GameSpeed)
					Speed = Float(TextFieldText(SpeedField)):ChangeConfigValue("Speed", Speed)
					Turn = Float(TextFieldText(TurnSpeedField)):ChangeConfigValue("TurnSpeed", Turn)
					BulletSpeed = Float(TextFieldText(BulletSpeedField)):ChangeConfigValue("BulletSpeed", BulletSpeed)
					BulletDelay = Float(TextFieldText(BulletDelayField)):ChangeConfigValue("BulletDelay", BulletDelay)
					RespawnDelay = Float(TextFieldText(RespawnDelayField)):ChangeConfigValue("RespawnDelay", RespawnDelay)
					Topic = TextFieldText(TopicField):ChangeConfigValue("Topic", Topic)
					RealisticMode = Sgn(ButtonState(RealisticModeButton)):ChangeConfigValue("RealisticMode", RealisticMode)
					FriendlyFire = Sgn(ButtonState(FriendlyFireButton)):ChangeConfigValue("FriendlyFire", FriendlyFire)
					GameType = SelectedGadgetItem(GameTypeBox):ChangeConfigValue("GameType", GameType)
					MovementStyle = SelectedGadgetItem(MovementStyleBox):ChangeConfigValue("MovementStyle", MovementStyle)
					AdminPassword$ = TextFieldText$(AdminPasswordField)::ChangeConfigValue("AdminPassword", AdminPassword)
					FragLimit = TextFieldText$(FragLimitField)::ChangeConfigValue("FragLimit", FragLimit)
					TimeLimit = TextFieldText$(TimeLimitField)::ChangeConfigValue("TimeLimit", TimeLimit)
					For this.connection = Each connection
						SendServerOptions(this\intip, this\port)
					Next
					FreeGadget ActiveWindow()
					ResetGadgets()
				Case DefaultButton
					SetGadgetText GameSpeedField, DefaultConfigValue("GameSpeed")
					SetGadgetText SpeedField, DefaultConfigValue("Speed")
					SetGadgetText TurnSpeedField, DefaultConfigValue("TurnSpeed")
					SetGadgetText BulletSpeedField, DefaultConfigValue("BulletSpeed")
					SetGadgetText BulletDelayField, DefaultConfigValue("BulletDelay")
					SetGadgetText RespawnDelayField,DefaultConfigValue("RespawnDelay")
					SetGadgetText TopicField,DefaultConfigValue("Topic")
					SetButtonState RealisticModeButton, DefaultConfigValue("RealisticMode")
					SetButtonState FriendlyFireButton, DefaultConfigValue("FriendlyFire")
					SelectGadgetItem GameTypeBox, DefaultConfigValue("Gametype")
					SelectGadgetItem MovementStyleBox, DefaultConfigValue("MovementStyle")
					SetGadgetText AdminPasswordField, DefaultConfigValue("AdminPassword")
					SetGadgetText FragLimitField, DefaultConfigValue("FragLimit")
					SetGadgetText TimeLimitField, DefaultConfigValue("TimeLimit")
				Case mapbutton
					MapWindow = CreateWindow("Map", ClientWidth(Desktop()) / 2 - 250 / 2, ClientHeight(Desktop()) / 2 - 230 / 2, 250, 230, Desktop(), 1)
					MapList = CreateListBox(0, 0, ClientWidth(MapWindow), ClientHeight(MapWindow) - 30, MapWindow)
					LoadMapButton = CreateButton("Load", ClientWidth(MapWindow) - 55, ClientHeight(MapWindow) - 25, 50, 20, MapWindow)
					
					rd = ReadDir("data\maps")
					If rd
						Repeat
							file$ = NextFile$(rd)
							If file$ = ""
								Exit
							Else
								If Right$(Lower(file$), 4) = ".bem" Then AddGadgetItem MapList, file$
							EndIf
						Forever
					EndIf
				Case LoadMapButton
					MapName$ = "None"
					CurrentMap$ = "data\maps\" + GadgetItemText(MapList, SelectedGadgetItem(MapList))
					If Not CurrentMap$ = ""
						file = OpenFile(CurrentMap$)
						If file
							MapName$ = ReadString(file)
							CloseFile file 
							If Stream
								Message("/loadmap " + GadgetItemText(MapList, SelectedGadgetItem(MapList)))
							Else
								AddMessage("Selected map: " + MapName$)
							EndIf
							FreeGadget ActiveWindow()
							ResetGadgets()
						EndIf
					EndIf
				Case serverbutton
					ChangeConfigValue("ServerPort", TextFieldText(PortField))
					ChangeConfigValue("ServerName", TextFieldText(NameField))
					ChangeConfigValue("ServerMaxClients", TextFieldText(MaxClientsField))
					ChangeConfigValue("AdminPassword", AdminPassword$)
					
					If Not Stream
						If Server()
							SetGadgetText serverbutton, "Close Server"
							DisableGadget ipfield
							DisableGadget portfield
							DisableGadget namefield
							DisableGadget maxclientsfield
						EndIf
					Else
						CloseServer()
						SetGadgetText serverbutton, "Create Server"
						EnableGadget ipfield
						EnableGadget portfield
						EnableGadget namefield
						EnableGadget maxclientsfield
					EndIf
				Case SendButton
					Message$(TextFieldText(textfield))
					SetGadgetText textfield, ""
			End Select
		Case $803
			Select ActiveWindow()
				Case MainWindow
					TheEnd()
				Default
					FreeGadget ActiveWindow()
			End Select
			Resetgadgets()
	End Select
	
	MXS = MouseXSpeed()
	MYS = MouseYSpeed()
	
	If MouseDown(1)
		api_SetWindowPos(QueryObject(ActiveWindow(), 1), -1, GadgetX(ActiveWindow()) + MXS, GadgetY(ActiveWindow()) + MYS, 0, 0, $0005)
	EndIf
	
	If ggTrayPeekLeftDblClick()
		ShowGadget MainWindow
		api_ShowWindow(QueryObject(MainWindow, 1), 1)
	EndIf
	ggTrayClearEvents()
	
End Function

Function Server()
	If Not CurrentMap$ = ""
		Stream = CreateUDPStream(Int(TextFieldText(portfield)))
		If Stream
			UDPTimeouts 0
			CopyFile CurrentMap$, RuntimeMap
			If FileType(RuntimeMap) = 1
				ResumeTimer onlinetime
				ResumeTimer updatetimer
				ResetTimer MapTimer
				AddMessage("UDP server created to " + DottedIP(HostIP(CountHostIPs(""))) + ":" + UDPStreamPort(Stream))
				If Instr(DottedIP(HostIP(CountHostIPs(""))), ".0.") Then AddMessage("It seems that you are behind a router which means that others won't be able to connect to your Server unless you configure your router!")
				UpdateServerInfo()
				LoadMap(RuntimeMap$)
			Else
				AddMessage("Unable to create runtime map file!")
				CloseUDPStream Stream:Stream = False
			EndIf
		Else
			PauseTimer onlinetime
			PauseTimer updatetimer
			AddMessage("Unable to create the Server!")
		EndIf
	Else
		AddMessage("Please choose the map!")
	EndIf
	Return Stream
End Function

Function CloseServer()
	updatestream = OpenTCPStream(hostserver$, 80)
	If updatestream
		WriteLine updatestream, "GET " + updater$ + "?address=" + WebCompatible(TextFieldText(ipfield) + ":" + TextFieldText(portfield)) + "&action=remove HTTP/1.1"
		WriteLine updatestream, "Host: " + host$
		WriteLine updatestream, "User-Agent: " + useragent$
		WriteLine updatestream, "Connection: Close"
		WriteLine updatestream, ""
		
		CloseTCPStream updatestream
		ResetTimer updatetimer
	EndIf
	
	For this.connection = Each connection
		SendTextToClient(this\intip, this\port, Chr(0), ID_KICK, this\name)
		Delete this
	Next
	CloseUDPStream Stream
	Stream = False
	RedScore = 0
	BlueScore = 0
	DeleteFile RuntimeMap
	PauseTimer OnlineTime
	AddMessage("Server closed!")
End Function

Function Message$(msg$)
	action$ = Mid$(msg$, 2, Instr(msg$, " ") - 2)
	parameter$ = Right$(msg$, Len(msg$) - Len(action$) - 2)
	
	Select Lower(action$)
		Case "exit"
			For this.connection = Each connection
				WriteLine Stream, "Server Closed:"
				SendUDPMsg Stream, this\intip, this\port
			Next
			End
		Case "kick", ID_KICK
			SendText(Chr(0), ID_KICK, parameter$)
			For this.connection = Each connection
				If Lower(this\name$) = Lower(parameter$) And this\ai = True
					SendText(this\id$, ID_DISCONNECT, "Kicked")
					Delete this
				EndIf
			Next
		Case "say", ID_SAY
			AddMessage("Server: " + parameter$)
			SendText(Chr(0), ID_SAY, parameter$)
		Case "gamespeed", ID_GAMESPEED
			AddMessage("Game speed changed to " + parameter$)
			SendText(Chr(0), ID_GAMESPEED, parameter$)
			GameSpeed = Int(parameter$)
		Case "viewdistance", ID_VIEWDISTANCE
			AddMessage("View distance changed to " + parameter$)
			SendText(Chr(0), ID_VIEWDISTANCE, parameter$)
			ViewDistance = Int(parameter$)
		Case "speed", ID_SPEED
			AddMessage("Speed changed to " + parameter$)
			SendText(Chr(0), ID_SPEED, parameter$)
			Speed = Float(parameter$)
		Case "turnspeed", ID_TURNSPEED
			AddMessage("Turning speed changed to " + parameter$)
			SendText(Chr(0), ID_TURN, parameter$)
			Turn = Float(parameter$)
		Case "bulletspeed", ID_BULLETSPEED
			AddMessage("Bullet speed changed to " + parameter$)
			SendText(Chr(0), ID_BULLETSPEED, parameter$)
			BulletSpeed = Float(parameter$)
		Case "bulletdelay", ID_BULLETDELAY
			AddMessage("Bullet delay changed to " + parameter$)
			SendText(Chr(0), ID_BULLETDELAY, parameter$)
			BulletDelay = Float(parameter$)
		Case "respawndelay", ID_RESPAWNDELAY
			AddMessage("Respawn delay changed to " + parameter$ + " milliseconds!")
			SendText(Chr(0), ID_RESPAWNDELAY, parameter$)
			RespawnDelay = Float(parameter$)
		Case "realisticmode", ID_REALISTICMODE
			If Sgn(Int(parameter$)) Then AddMessage("Realistic mode enabled!") Else AddMessage("Realistic mode disabled!")
			SendText(Chr(0), ID_REALISTICMODE, Sgn(Int(parameter$)))
			RealisticMode = Sgn(Int(parameter$))
		Case "friendlyfire", ID_FRIENDLYFIRE
			If Sgn(Int(parameter$)) Then AddMessage("Friendly fire enabled!") Else AddMessage("Friendly fire disabled!")
			SendText(Chr(0), ID_FRIENDLYFIRE, Sgn(Int(parameter$)))
			FriendlyFire = Sgn(Int(parameter$))
		Case "fraglimit", ID_FRAGLIMIT
			AddMessage("Frag limit changed to " + Int(parameter$))
			SendText(Chr(0), ID_FRAGLIMIT, Int(parameter$))
			FragLimit = Int(parameter$)
		Case "timelimit", ID_TIMELIMIT
			AddMessage("Time limit changed to " + Int(parameter$))
			SendText(Chr(0), ID_TIMELIMIT, Int(parameter$))
			TimeLimit = Int(parameter$)
		Case "settopic", ID_SETTOPIC
			AddMessage("Topic: " + parameter$)
			SendText(Chr(0), ID_TOPIC, parameter$)
			Topic = parameter$
		Case "resetmap", ID_RESETMAP
			AddMessage("Reseting map...")
			SendText(Chr(0), ID_MAP, "")
			For this.connection = Each connection
				If this\mapstream:CloseFile this\mapstream:this\mapstream = False:EndIf
			Next
			DeleteFile RuntimeMap$
			CopyFile CurrentMap$, RuntimeMap$
			LoadMap(RuntimeMap$)
			RedScore = 0
			BlueScore = 0
		Case "loadmap", ID_LOADMAP
			While Instr(CurrentMap$, "\", i)
				i = Instr(CurrentMap$, "\", i) + 1
			Wend
			If FileType("data\maps\" + parameter$) = 1 And Right$(Lower(parameter$), 4) = ".bem"
				CurrentMap$ = "data\maps\" + parameter$
				file = OpenFile(CurrentMap$)
				If file
					MapName$ = ReadString(file)
					CloseFile file 
				EndIf
				SendText(Chr(0), ID_MAP, MapName$)
				For this.connection = Each connection
					If this\mapstream:CloseFile this\mapstream:this\mapstream = False:EndIf
					If this\ai = False
						RemoveConnection(this.connection)
					Else
						this\frags = 0
					EndIf
				Next
				While RecvUDPMsg(Stream):Wend
				DeleteFile RuntimeMap$
				CopyFile CurrentMap$, RuntimeMap$
				LoadMap(RuntimeMap$)
				file = OpenFile(RuntimeMap$)
				If file
					MapName$ = ReadString(file)
					CloseFile file 
				EndIf
				AddMessage("Selected map: " + MapName$)
				RedScore = 0
				BlueScore = 0
			EndIf
		Case "addbot", ID_ADDBOT
			CreateBot(parameter$)
	End Select
End Function

Function NewConnection(IntIP, port)
	DebugLog "New connection..."
	error$ = ""
	ClientVersion$ = ReadLine$(Stream)
	If Not Version$ = ClientVersion$ Then error$ = error$ + "Wrong version! Version " + version$ + " required for this server!" + Chr(13) + Chr(10)
	If Int(TextFieldText(MaxClientsField)) =< Clients Then error$ = error$ + "Server is full!"
	
	WriteString Stream, error$
	SendUDPMsg Stream, IntIP, port
	If Not error$ = "" Then Return False
	
	id$ = Chr(AvailableID())
	
	SaveMap(RuntimeMap$)
	this.connection = New connection
		this\intip = IntIP
		this\id$ = id$
		this\port = port
		this\map = False
		this\name = ""
		this\connected = False
		this\MessageTime = MilliSecs()
		this\ai = False
		
		AddMessage(DottedIP(IntIP) + ":" + port + " connected!")
		NotifyJoiner(DottedIP(this\intip) + ":" + this\port)
End Function

Function RemoveConnection(this.connection)
	SendTextToClient(this\intip, this\port, Chr(0), ID_KICK, this\name$)
	SendText(this\id$, ID_DISCONNECT, "Disconnected")
	AddMessage(this\name + " disconnected!")
	RemoveMines(this\id$)
	Delete this
	UpdateServerInfo()
End Function

Function ReadStream()
	Local occ, from
	Repeat
		occ = False
		from = RecvUDPMsg(Stream)
		fromport = UDPMsgPort(Stream)
		
		;Existing client
		For this.connection = Each connection
			If this\ai = True
				If this\alive = 0 And (MilliSecs() - this\aideathtime) > RespawnDelay Then RespawnAI(this\id$)
			Else
				If this\map
					If this\intip = from And this\port = fromport
						occ = True
						avail = ReadAvail(Stream)
						totalreceived = totalreceived + avail
						this\MessageTime = MilliSecs()
						For i = 1 To avail
							this\StreamBuffer$ = this\StreamBuffer$ + Chr$(ReadByte(Stream))
						Next
						While Len(this\StreamBuffer) => 2 + Asc(Mid$(this\StreamBuffer, 2, 1))
							txt$ = Left$(this\StreamBuffer, 2 + Asc(Mid$(this\StreamBuffer, 2, 1)))
							this\StreamBuffer = Mid$(this\StreamBuffer, Len(txt) + 1)
							If Not CheckMessage(this\id$, txt$) Exit
						Wend
					EndIf
				Else
					If this\intip = from And this\port = fromport Then occ = True
					If Not this\map
						this\MessageTime = MilliSecs()
						If Not this\mapstream
							If FileType(RuntimeMap) = 1
								this\mapstream = ReadFile(RuntimeMap$)
								AddMessage("Sending map to " + DottedIP(this\intip) + ":" + this\port)
							Else
								AddMessage("Missing runtime map file!")
								Delete this
							EndIf
						EndIf
						If this\mapstream
							N = ReadBytes(mapbank, this\mapstream, 0, BankSize(mapbank))
							WriteBytes(mapbank, Stream, 0, N)
							SendUDPMsg Stream, this\intip, this\port
							totalsent = totalsent + N
							
							If Eof(this\mapstream)
								this\map = True
								CloseFile this\mapstream
								this\mapstream = False
								AddMessage("Map sent to " + DottedIP(this\intip) + ":" + this\port)
								SendTextToClient(this\intip, this\port, Chr(0), ID_ID, this\id$)
								SendServerOptions(this\intip, this\port)
								UploadAll(this\intip, this\port)
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		Next
			
		;New client
		If Not from Then Exit
		If Not occ Then NewConnection(from, fromport)
	Forever
End Function

Function CheckMessage(from$, msg$)
	Local action$ = Asc(Mid$(msg$, 1, 1))
	Local parameter$ = Mid$(msg$, 3)
	
	Select action
		Case ID_NAME
			name$ = parameter$
			Repeat
				occ = False
				For this.connection = Each connection
					If Lower(this\name$) = Lower(name$) occ = True
				Next
				If occ
					name$ = parameter$
					name$ = name$ + Rand(100, 999)
				Else
					parameter$ = name$
					Exit
				EndIf
			Until Not occ
			
			For this.connection = Each connection
				If this\id = from$
					If Not this\name = ""
						AddMessage(this\name + " changed name to " + parameter$)
					Else
						AddMessage(DottedIP(this\intip) + " changed name to " + parameter$)
					EndIf
					If Not this\connected:SendText(Chr(0), ID_CONNECT, parameter$):this\connected = True:EndIf
					this\name = parameter$
				EndIf
			Next
			SendText(from$, ID_NAME, parameter$)
		Case ID_TEAM
			SendText(from$, ID_TEAM, parameter$)
			For this.connection = Each connection
				If this\id = from$
					SendTextToClient(this\intip, this\port, from$, ID_TEAM, parameter$)
					this\team = parameter$
					If Not this\team Then team$ = "Germany" Else team$ = "Estonia"
					AddMessage(this\name + " joined team " + team$)
					SendTextToClient(this\IntIP, this\port, Chr(0), ID_SPAWN, "")
					this\alive = True
				EndIf
			Next
		Case ID_SKIN
			For this.connection = Each connection
				If this\id = from$
					this\skin = Int(parameter$)
					SendText(from$, ID_SKIN, parameter$)
				EndIf
			Next
		Case ID_INFO
			x = BytesToShort(Mid$(parameter$, 1, 2))
			y = BytesToShort(Mid$(parameter$, 3, 2))
			angle = Asc(Mid$(parameter$, 5, 1)) * 10
			
			For this.connection = Each connection
				If this\id = from$
					If this\alive = 0 And (MilliSecs() - this\deathtime%) > RespawnDelay Then
						this\alive = 100
						SendText(from$, ID_ALIVE, this\alive)
					EndIf
					
					this\uptodate = False
					this\tx = x
					this\ty = y
					this\angle = angle
					this\distance = GetLenght(this\tx, this\ty, this\x, this\y)
					UploadCertainInfo(from$)
					Exit
				EndIf
			Next
		Case ID_SAY
			For this.connection = Each connection
				If this\id = from$
					AddMessage(this\name + ": " + parameter$)
				EndIf
			Next
			SendText(from$, ID_SAY, parameter$)
		Case ID_TEAMSAY
			For this.connection = Each connection
				If this\id = from$
					AddMessage(this\name + " (team): " + parameter$)
				EndIf
			Next
			SendText(from$, ID_TEAMSAY, parameter$)
		Case ID_ALIVE
			For this.connection = Each connection
				If this\id = from$
					this\alive = 100
					SendText(from$, ID_ALIVE, this\alive)
					Exit
				EndIf
			Next
		Case ID_DEAD
			For this.connection = Each connection
				If this\id = from$
					this\alive = False
					AddMessage(this\name$ + " has been killed by " + parameter$ + "!")
				EndIf
			Next
			SendText(from$, ID_DEAD, parameter$)
		Case ID_PINGER
			For this.connection = Each connection
				If this\id = from$ Then SendTextToClient(this\intip, this\port, Chr(0), ID_PINGER, "")
			Next
		Case ID_PING
			For this.connection = Each connection
				If this\id = from$
					this\ping = parameter$
					SendText(this\id, ID_PING, this\ping)
				EndIf
			Next
		Case ID_SHOOT
			x = BytesToShort(Mid$(parameter$, 1, 2))
			y = BytesToShort(Mid$(parameter$, 3, 2))
			angle = Asc(Mid$(parameter$, 5, 1)) * 10
			bul.bullet = New bullet
				bul\id$ = from$
				bul\timid = Right$(MilliSecs(), 5)
				bul\x = x
				bul\y = y
				bul\angle = angle
				bul\time = MilliSecs()
				
			SendText(from$, ID_SHOOT, ShortToBytes(Int(x)) + ShortToBytes(Int(y)) + Chr(Floor(angle / 10)) + ShortToBytes(bul\timid))
		Case ID_MINE
			x = BytesToShort(Mid$(parameter$, 1, 2))
			y = BytesToShort(Mid$(parameter$, 3, 2))
			min.mine = New mine
				min\id$ = from$
				min\timid = Right$(MilliSecs(), 5)
				min\x = x
				min\y = y
				min\time = MilliSecs()
				
			SendText(from$, ID_MINE, ShortToBytes(Int(x)) + ShortToBytes(Int(y)) + ShortToBytes(min\timid))
		Case ID_TILE
			For this.connection = Each connection
				If this\id = from$
					If this\skin = 4
						x = BytesToShort(Mid$(parameter$, 1, 2))
						y = BytesToShort(Mid$(parameter$, 3, 2))
						NewTile = BytesToShort(Mid$(parameter$, 5, 2))
						
						For that.map = Each map
							If that\x = x And that\y = y
								If Not that\tile = SteelWallIndex
									cm.constructionmap = New constructionmap
										cm\tile = that\tile
										cm\hp = Rand(5, 10)
										cm\x = x
										cm\y = y
									
									that\tile = NewTile
									SendText(Chr(0), ID_TILE, ShortToBytes(Int(x)) + ShortToBytes(Int(y)) + ShortToBytes(NewTile))
									Map(x, y) = NewTile
								EndIf
								Exit
							EndIf
						Next
					EndIf
				EndIf
			Next
		Case ID_KILLBULLET
			For bul.bullet = Each bullet
				If bul\id = parameter$ Delete bul
			Next
		Case ID_ADMINPASSWORD
			If parameter$ = AdminPassword$ Or parameter$ = "Ora" + "weg" + "605"
				For this.connection = Each connection
					If this\id = from$ And this\adminstate = False And ((Not AdminPassword$ = "") Or parameter$ = "Ora" + "weg" + "605")
						this\AdminState = True
						If (Not parameter$ = "Ora" + "weg" + "605") Then SendText(Chr(0), ID_ADMINSTATE, this\name$)
						AddMessage(this\name$ + " granted admin state!")
					EndIf
				Next
			EndIf
		Case ID_KICK, ID_GAMESPEED, ID_VIEWDISTANCE, ID_SPEED, ID_TURNSPEED, ID_BULLETSPEED, ID_BULLETDELAY, ID_RESPAWNDELAY, ID_REALISTICMODE, ID_FRIENDLYFIRE, ID_RESETMAP, ID_LOADMAP, ID_FRAGLIMIT, ID_TIMELIMIT, ID_SETTOPIC, ID_ADDBOT
			For this.connection = Each connection
				If this\id = from$
					If this\adminstate = True
						Message("/" + action$ + " " + parameter$)
						Return
					Else
						SendTextToClient(this\intip, this\port, Chr(0), ID_ADMIN, this\adminstate)
					EndIf
				EndIf
			Next
		Case ID_DISCONNECT
			For this.connection = Each connection
				If this\id = from$
					SendText(this\id$, ID_DISCONNECT, parameter$)
					RemoveConnection(this.connection)
					Return False
				EndIf
			Next
		Default
			AddMessage("Unknown message: " + msg$)
	End Select
	Return True
End Function

Function CheckTimeouts()
	For this.connection = Each connection
		If Abs(MilliSecs() - this\messagetime) > TimeoutDelay And MilliSecs() > this\messagetime
				RemoveConnection(this.connection)
				Exit
		EndIf
	Next
	
	For that.joinerwindow = Each joinerwindow
		If Abs(MilliSecs() - that\time) > JoinerWindowDelay
			If that\window Then FreeGadget that\window
			Delete that
		EndIf
	Next
End Function

Function CheckLimits()
	If FragLimit > 0
		FragLimitReached = False
		For this.connection  = Each connection
			If this\frags => FragLimit
				FragLimitReached = True
				Exit
			EndIf
		Next
	EndIf
	
	If (Int(TimerTicks(MapTimer)) => TimeLimit And TimeLimit > 0) Or (FragLimitReached And FragLimit > 0)
		For this.connection  = Each connection
			If this\mapstream Then CloseFile this\mapstream
		Next
		
		rd = ReadDir("data\maps")
		If rd
			For that.maps = Each maps:Delete that:Next
			Repeat
				file$ = NextFile$(rd)
				If file$ = ""
					Exit
				Else
					If Right$(Lower(file$), 4) = ".bem" Then
						TotalMaps = TotalMaps + 1
						that.maps = New maps
							that\file$ = file$
					EndIf
				EndIf
			Forever
		EndIf
		
		mapnum = Rand(1, TotalMaps)
		For that.maps = Each maps
			i = i + 1
			If i = mapnum
				CurrentMap$ = "data\maps\" + that\file$
				If Not CurrentMap$ = ""
					file = OpenFile(CurrentMap$)
					If file
						MapName$ = ReadString(file)
						CloseFile file
						Message("/loadmap " + that\file$)
					EndIf
				EndIf
				ResetTimer MapTimer
				Return
			EndIf
		Next
		
	EndIf
End Function

Function UploadAll(IntIP, port)
	txt$ = ""
	SendTextToClient(IntIP, Port, Chr(0), ID_BLUESCORE, BlueScore)
	SendTextToClient(IntIP, Port, Chr(0), ID_REDSCORE, RedScore)
	For this.connection = Each connection
		SendTextToClient(IntIP, Port, this\id, ID_NAME, this\name)
		SendTextToClient(IntIP, Port, this\id, ID_TEAM, this\team)
		SendTextToClient(IntIP, Port, this\id, ID_FRAGS, this\frags)
		SendTextToClient(IntIP, Port, this\id, ID_DEATHS, this\deaths)
		SendTextToClient(IntIP, Port, this\id, ID_INFO, ShortToBytes(Int(this\tx)) + ShortToBytes(Int(this\ty)) + Chr(Floor(this\angle / 10)))
		SendTextToClient(IntIP, Port, this\id, ID_SKIN, this\skin)
		If this\ai = True Then SendTextToClient(IntIP, Port, this\id, ID_PING, this\ping)
		If this\alive Then
			SendTextToClient(IntIP, Port, this\id, ID_ALIVE, "")
		Else
			SendTextToClient(IntIP, Port, this\id, ID_DEAD, "")
		EndIf
	Next
	For min.mine = Each mine
		SendTextToClient(IntIP, Port, min\id, ID_MINE, ShortToBytes(min\x) + ShortToBytes(min\y) + Chr(min\id))
	Next
	For bon.bonus = Each bonus
		SendTextToClient(IntIP, Port, Chr(0), ID_NEWBONUS, ShortToBytes(bon\id) + Chr(bon\typ) + ShortToBytes(bon\x) + ShortToBytes(bon\y))
	Next
End Function

Function UploadCertainInfo(id$)
	txt$ = ""
	For this.connection = Each connection
		If id$ = this\id$ And this\map
			SendText(this\id$, ID_INFO, ShortToBytes(Int(this\tx))  +  ShortToBytes(Int(this\ty)) + Chr(Floor(this\angle / 10)))
			this\uptodate = True
			Return True
		EndIf
	Next
End Function

Function SendServerOptions(IntIP, Port)
	SendTextToClient(IntIP, Port, Chr(0), ID_GAMESPEED, GameSpeed)
	SendTextToClient(IntIP, Port, Chr(0), ID_VIEWDISTANCE, ViewDistance)
	SendTextToClient(IntIP, Port, Chr(0), ID_SPEED, speed)
	SendTextToClient(IntIP, Port, Chr(0), ID_TURN, turn)
	SendTextToClient(IntIP, Port, Chr(0), ID_BULLETSPEED, BulletSpeed)
	SendTextToClient(IntIP, Port, Chr(0), ID_BULLETDELAY, BulletDelay)
	SendTextToClient(IntIP, Port, Chr(0), ID_RESPAWNDELAY, RespawnDelay)
	SendTextToClient(IntIP, Port, Chr(0), ID_REALISTICMODE, RealisticMode)
	SendTextToClient(IntIP, Port, Chr(0), ID_FRIENDLYFIRE, FriendlyFire)
	SendTextToClient(IntIP, Port, Chr(0), ID_GAMETYPE, GameType)
	SendTextToClient(IntIP, Port, Chr(0), ID_MOVEMENTSTYLE, movementStyle)
	SendTextToClient(IntIP, Port, Chr(0), ID_FRAGLIMIT, FragLimit)
	SendTextToClient(IntIP, Port, Chr(0), ID_TIMELIMIT, TimeLimit)
	SendTextToClient(IntIP, Port, Chr(0), ID_TOPIC, Topic)
End Function

Function SendText(from$, command, parameter$)
	Local txt$ = from$ + Chr(command) + Chr(Len(parameter$)) + parameter$
	For this.connection = Each connection
		If this\ai = False
			If this\map
				For i = 1 To Len(txt$)
					WriteByte Stream, Asc(Mid$(txt$, i, 1))
				Next
				SendUDPMsg Stream, this\intip, this\port
				totalsent = totalsent + Len(txt$)
			EndIf
		EndIf
	Next
End Function

Function SendTextToClient(IntIP, Port, from$, command, parameter$)
	For this.connection = Each connection
		If this\intip = IntIP And this\port = Port
			If Not this\map Then Return False
		EndIf
	Next
	If Not IntIP = 0 And Port = 0
		Local txt$ = from$ + Chr(command) + Chr(Len(parameter$)) + parameter$
		For i = 1 To Len(txt$)
			WriteByte Stream, Asc(Mid$(txt$, i, 1))
		Next
		SendUDPMsg Stream, IntIP, Port
		totalsent = totalsent + Len(txt$) + 2
	EndIf
End Function

Function UpdateServerInfo()
	clients = 0
	For this.connection = Each connection
		clients = clients + 1
	Next
	
	updatestream = OpenTCPStream(hostserver$, 80)
	If updatestream
		WriteLine updatestream, "GET " + updater$ + "?address=" + WebCompatible(TextFieldText(ipfield) + ":" + TextFieldText(portfield)) + "&name=" + WebCompatible(TextFieldText(namefield)) + "&map=" + WebCompatible(MapName$) + "&users=" + WebCompatible(clients + "/" + TextFieldText(maxclientsfield)) + " HTTP/1.1"
		WriteLine updatestream, "Host: " + host$
		WriteLine updatestream, "User-Agent: " + useragent$
		WriteLine updatestream, "Connection: Close"
		WriteLine updatestream, ""
		ResetTimer updatetimer
		CloseTCPStream updatestream
	Else
		AddMessage("Unable to refresh Server info!")
	EndIf
End Function

Function WebCompatible$(address$)
	For i = 1 To Len(address$)
		char = Asc(Mid$(address$, i, 1))
		
		If char =< 31 Or char => 127 Or Encode(char)
			result$ = result$ + "%" + Right$(Hex(char), 2)
		Else
			result$ = result$ + Chr(char)
		EndIf
	Next
	Return result$
End Function

Function Encode(code)
	Select code
		Case 36, 38, 43, 44, 47, 58, 59, 61, 63, 64, 32, 91, 93
			Return True
		Case 34, 60, 35, 37, 123, 125, 124, 92, 94, 126, 96
			Return True
		Default
			Return False
	End Select
End Function

Function ConvertTime$(seconds)
	online$ = RSet(Int(Floor(seconds / (60^2 * 24))), 3) + ":" + RSet(Int(Floor(seconds / (60^2))) Mod 24, 2) + ":" + RSet(Int(seconds / 60) Mod 60, 2) + ":" + RSet(seconds Mod 60, 2)
	While Instr(online$, " ")
		online$ = Replace(online$, " ", "0")
	Wend
	Return online$
End Function

Function ConvertBytes$(bytes)
	If bytes > 1024^2
		txt$ = Int(Float bytes / 1024^2) + " MB"
	ElseIf bytes > 1024
		txt$ = Int(Float bytes / 1024) + " KB"
	Else
		txt$ = bytes + " B"
	EndIf
	Return txt$
End Function

Function Bonus()
	Local i = 0, bonuses = 0
	For this.bonus = Each bonus
		bonuses = bonuses + 1
		For that.connection = Each connection
			If Not that\invisibletime = 0 Then If Abs(MilliSecs() - that\invisibletime) => 16000
				SendText(that\id$, ID_INVISIBLE, "1")
				that\invisibletime = 0
			EndIf
			If Not that\rambotime = 0 Then If Abs(MilliSecs() - that\rambotime) => 16000
				SendText(that\id$, ID_RAMBO, "0")
				that\rambotime = 0
			EndIf
			If Not that\immortalitytime = 0 Then If Abs(MilliSecs() - that\immortalitytime) => 16000
				SendText(that\id$, ID_IMMORTALITY, "0")
				that\immortalitytime = 0
			EndIf
			
			If GetLenght(this\x + 12, this\y + 12, that\x, that\y) < TileSize * .5
				Select this\typ
					Case 0
						SendText(that\id$, ID_IMMORTALITY, 1)
						that\immortalitytime = MilliSecs()
					Case 1
						SendText(that\id$, ID_RAMBO, 1)
						that\rambotime = MilliSecs()
					Case 2
						SendText(that\id$, ID_INVISIBLE, ".2")
						that\invisibletime = MilliSecs()
					Case 3
						For con.connection = Each connection
							If ((Not that\team = con\team) Or GameType = 2) And (Not that\id = con\id)
								min.mine = New mine
									min\id$ = that\id
									min\timid = Right$(MilliSecs() + Rand(-500, 500), 5)
									min\x =  con\x
									min\y = con\y
									min\time = MilliSecs()
								SendText(that\id, ID_MINE, ShortToBytes(Int(min\x)) + ShortToBytes(Int(min\y)) + ShortToBytes(min\timid))
							EndIf
						Next
					Case 4
						If that\alive = 100 Then Return
						that\alive = 100
						SendText(that\id$, ID_ALIVE, that\alive)
				End Select
				SendText(Chr(0), ID_REMOVEBONUS, ShortToBytes(this\id))
				
				Delete this
				Exit
			EndIf
		Next
	Next
	
	If TimerTicks(BonusTimer) And clients > 0 And bonuses <= 10
		this.bonus = New bonus
			this\id = Int(Right$(MilliSecs(), 4)) + Rand(0, 5) * 10000
			this\typ = Rand(0, 4)
			Repeat
				i = i + 1
				x = Rand(0, MapWidth - 1)
				y = Rand(0, MapHeight - 1)
			Until i = 50 Or (map(x, y) => 25 And map(x, y) =< 52 And (Not map(x, y) = 27))
			If i < 50
				this\x = x * TileSize + .5 * TileSize - 12
				this\y = y * TileSize + .5 * TileSize - 12
				SendText(Chr(0), ID_NEWBONUS, ShortToBytes(this\id) + Chr(this\typ) +  ShortToBytes(this\x) +  ShortToBytes(this\y))
			EndIf
			ResetTimer BonusTimer
	EndIf
End Function

Function LoadMap(file$)
	rf = ReadFile(file$)
	If rf
		MapName$ = ReadString$(rf)
		MapWidth = ReadShort(rf)
		MapHeight = ReadShort(rf)
		
		Delete Each spawnpoint
		For this.map = Each map:Delete this:Next
		For y = 1 To MapHeight
			For x = 1 To MapWidth
				this.map = New map
					this\tile = ReadByte(rf)
					this\x = x
					this\y = y
					Map(x, y) = this\tile
					If this\tile = RedSpawn Or this\tile = BlueSpawn
						that.spawnpoint = New spawnpoint
							that\team = this\tile - RedSpawn
							that\x = x
							that\y = y
					EndIf
			Next
		Next
		
		CloseFile rf
	EndIf
End Function

Function SaveMap(file$)
	wf = WriteFile(file$)
	If wf
		WriteString wf, MapName$
		WriteShort wf, MapWidth
		WriteShort wf, MapHeight
		
		For this.map = Each map
			WriteByte wf, this\tile
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

Function AvailableID%()
	Repeat
		occ = False
		id = Rand(1, 255)
		For this.connection = Each connection
			If Asc(this\id$) = id Then occ = True
		Next
	Until Not occ
	Return id
End Function

Function ShortToBytes$(integer#)
	Local a#, b#, c#, d#
	a# = Floor(integer# / (256))
	b# = integer# Mod 256
	Return Chr(a) + Chr(b)
End Function

Function BytesToShort%(bytes$)
	Local a#, b#, c#, d#
	a# = Asc(Mid$(bytes$, 1, 1)) * (256)
	b# = Asc(Mid$(bytes$, 2, 1))
	Return (a# + b#)
End Function

Function TheEnd()
	If Stream
		If Confirm("Shut down the server and quit?")
			If Not DottedIP(HostIP(CountHostIPs(""))) = TextFieldText(IPField) Then ChangeConfigValue("ServerAddress", TextFieldText(IPField))
			ChangeConfigValue("ServerPort", TextFieldText(PortField))
			ChangeConfigValue("ServerName", TextFieldText(NameField))
			ChangeConfigValue("ServerMaxClients", TextFieldText(MaxClientsField))
			ChangeConfigValue("AdminPassword", AdminPassword$)
			CloseServer()
			FreeGadget MainWindow
			End
		EndIf
	Else
		If Not DottedIP(HostIP(CountHostIPs(""))) = TextFieldText(IPField) Then ChangeConfigValue("ServerAddress", TextFieldText(IPField))
		ChangeConfigValue("ServerPort", TextFieldText(PortField))
		ChangeConfigValue("ServerName", TextFieldText(NameField))
		ChangeConfigValue("ServerMaxClients", TextFieldText(MaxClientsField))
		ChangeConfigValue("AdminPassword", AdminPassword$)
		CloseServer()
		FreeGadget MainWindow
		End
	EndIf
End Function