SeedRnd MilliSecs()
;ChangeDir(SystemProperty$("appdir"))

If Not FileType("data\registry.reg") Then CreateProtocolRegistryFile("data\registry.reg", "Battle Empire protocol", "be", SystemProperty$("appdir") + "be.exe")
CreateProcess("REG IMPORT data\registry.reg")

Type server
	Field address$
	Field name$
	Field map$
	Field clients$
End Type

Type player
	Field alive%
	Field id$
	Field name$
	Field team
	Field frags%
	Field deaths%
	Field x#
	Field y#
	Field tx#
	Field ty#
	Field visibility#
	Field invisible#
	Field immortality%, immortalityframe#
	Field rambo%
	
	Field distance#
	Field angle#
	Field tangle#
	Field frame#
	Field skin
	Field ping$
End Type
Dim PlayerList$(100, 6); alive, name, frags, deaths, team, ping

Type bonus
	Field id%
	Field typ%
	Field x%, y%
End Type

Type bullet
	Field id%
	Field ip$
	Field x#
	Field y#
	Field angle#
End Type

Type mine
	Field id%
	Field ip$
	Field x%
	Field y%
End Type

Type explosion
	Field frame#
	Field x
	Field y
End Type

Type Smoke
	Field frame#
	Field frames%
	Field x
	Field y
End Type

Type Particle
	Field x#
	Field y#
	Field angle#
	Field particleangle#
	Field turn#
	Field tile%
End Type

Type SpawnPoint
	Field team
	Field x
	Field y
End Type

Type file
	Field txt$
End Type

Include "TBMFunctions.bb"
Include "variables.bb"
Include "menu.bb"
If MainWindow FreeGadget MainWindow

AppTitle "Battle Empire " + version$
Graphics 640, 480, ConfigValue("ColorDepth"), 2 - Int(ConfigValue("Fullscreen"))
AppIcon("be.exe")
SetBuffer BackBuffer()
SetFont LoadFont("Courier New", 16)
Global MenuFont = LoadFont("System", 16)
TFormFilter False
Include "functions.bb"
Include "ProtocolCommands.bb"

;Sounds
Global EngineSound = LoadSound("data\sounds\engine.wav"):LoopSound EngineSound
Global JoinSound = LoadSound("data\sounds\join.wav")
Global MessageSound = LoadSound("data\sounds\message.wav")
Global BattleSound = LoadSound("data\sounds\battle.wav"):LoopSound BattleSound
Global ShotSound = LoadSound("data\sounds\shot.wav")
Global MineSound = LoadSound("data\sounds\mine.wav")
Global HitSound = LoadSound("data\sounds\hit.wav")
Global CrowSound = LoadSound("data\sounds\crow.wav")
Global OwlSound = LoadSound("data\sounds\owl.wav")
Global ScoreSound = LoadSound("data\sounds\score.wav")

Include "images.bb"
Include "environment.bb"
Include "bullet.bb"

;GUI
Include "gui.bb"
MainWindow = CreateGuiWindow("Menu", GraphicsWidth() / 2 - 100 / 2, GraphicsHeight() / 2 - 135 / 2, 100, 135)
Global HideButton = CreateGuiButton("Hide", 10, 25, 80, 20, MainWindow)
Global ServerButton = CreateGuiButton("Server", 10, 50, 80, 20, MainWindow)
OptionsButton = CreateGuiButton("Options", 10, 75, 80, 20, MainWindow)
Global ExitButton = CreateGuiButton("Exit", 10, 100, 80, 20, MainWindow)

;Start sounds
Global BattleSoundChannel = PlaySound(BattleSound):ChannelVolume BattleSoundChannel, GeneralVolume
PlayerEngineSound = PlaySound(EngineSound):ChannelVolume PlayerEngineSound, GeneralVolume
Global Music = PlayMusic("data\sounds\best.mp3", 1):ChannelVolume Music, MusicVolume

;Tank attributes
Global TankSpeedInfluence#
Global TankTurnInfluence#
Global TankReloadInfluence#
UpdateInfluences()

While True
	Debug("Calculate multiplier...", True)
	framedelay = (MilliSecs() - last_milli)
	multiplier# = Float framedelay / (1000 / game_speed):If multiplier# > 1 Then multiplier# = 1
	last_milli = MilliSecs()
	
	Debug("Decrease respawn time...")
	If Not PlayerAlive
		PlayerRespawnTime = PlayerRespawnTime - framedelay
		If PlayerRespawnTime =< 0 Then Respawn()
	EndIf
	
	CheckStream()
	
	;Music
	Debug("Loop music...")
	If Not ChannelPlaying(Music) Then Music = PlayMusic("data\sounds\best.mp3", 1):ChannelVolume Music, MusicVolume
	
	;Send ping & info
	Debug("Ping info...")
	If (Not PlayerUpToDate) Then If TimerTicks(InfoTimer) UpdateInfo()
	If Abs(MilliSecs() - PingTime) > PingDelay SendPingInfo()
	
	;Menu
	Debug("Display menu...")
	If KeyHit(1) DisplayMenu = 1 - DisplayMenu:ShowPointer
	Debug("Inputs...")
	Include "inputs.bb"
	
	;Camera
	Debug("Position camera...")
	If PlayerAlive
		CameraX = PlayerX - GraphicsWidth() / 2
		CameraY = PlayerY - GraphicsHeight() / 2
	Else
		CameraX = (Float CameraX * (24 * multiplier#) + (MapWidth * TileSize) * (Float MouseX() / GraphicsWidth()) - GraphicsWidth() / 2) / (24 * multiplier# + 1)
		CameraY = (Float CameraY * (24 * multiplier#) + (MapHeight * TileSize) * (Float MouseY() / GraphicsHeight()) - GraphicsHeight() / 2) / (24 * multiplier# + 1)
	EndIf
	
	If CameraX < 0 Then CameraX = 0 ElseIf CameraX > MapWidth * TileSize - GraphicsWidth() Then CameraX = MapWidth * TileSize - GraphicsWidth()
	If CameraY < 0 Then CameraY = 0 ElseIf CameraY > MapHeight * TileSize - GraphicsHeight() Then CameraY = MapHeight * TileSize - GraphicsHeight()
	
	Debug("Environment...")
	Environment()
	
	;Display messages
	Debug("Display messages...")
	For i = 1 To MessageBufferSize
		Color 0, 0, 0
		Text 3 - 1, 3 + (i - 1) * FontHeight() - 1, MessageBuffer(i, 1)
		col$ = MessageBuffer(i, 2)
		Color UnHex%(Mid$(col$, 1, 2)), UnHex%(Mid$(col$, 3, 2)), UnHex%(Mid$(col$, 5, 2))
		Text 3, 3 + (i - 1) * FontHeight(), MessageBuffer(i, 1)
	Next
	
	;Display respawn time
	Debug("Display respawn time...")
	If Not PlayerAlive
		Color 0, 0, 0
		Text GraphicsWidth() / 2 - 1, GraphicsHeight() / 2 - 1, "Respawning in " + Int(Ceil(PlayerRespawnTime / 1000) + 1) + " seconds!", True, True
		Color 0, 255, 255
		Text GraphicsWidth() / 2, GraphicsHeight() / 2, "Respawning in " + Int(Ceil(PlayerRespawnTime / 1000) + 1) + " seconds!", True, True
	EndIf
	
	;Scores
	Debug("Message bar...")
	If Not PlayerMessage$ = ""
		DrawTBMIMage(Bar, 2, GraphicsHeight() - TBMImageHeight(Bar) - 5)
		
		Color 200, 200, 200
		DrawImage TeamImages, 565, 410, 0
		DrawImage TeamImages, 610, 410, 1
		Text 595, 418, RedScore + ":" + BlueScore, 1, 1
		
		;Reload
		Color 200, 200, 0
		r# = (Float(MilliSecs() - BulletTime) / (BulletDelay * TankReloadInfluence#)):If r# > 1 Then r# = 1
		Rect 565, 427, 60 * r#, 10, True
		Color 200, 200, 200
		Rect 565, 427, 60, 10, False
		
		Color 200, 200, 200
		Text 565, 440, "Ping:" + PlayerPing
		Text 565, 455, "FPS:" + Int(FPS)
		
		Color 250, 250, 250
		Text 10, GraphicsHeight() - 22, PlayerMessage$
	Else
		;Status
		Color 0, 0, 0
		Text 623 - StringWidth(PlayerFrags), 425, PlayerFrags
		Color 250, 250, 250
		Text 624 - StringWidth(PlayerFrags), 426, PlayerFrags
		
		;Health
		PlayerHealth = (PlayerAlive + PlayerHealth + PlayerHealth) / 3
		Color 0, 0, 0
		Rect 564, 441, 62, 12, True
		Color 200, 0, 0
		r# = Int(PlayerHealth) / 100.0:If r# > 1 Then r# = 1 ElseIf r# < 0 Then r# = 0
		Rect 565, 442, 60 * r#, 10, True
		Color 200, 200, 200
		Rect 565, 442, 60, 10, False
		
		;Reload
		Color 0, 0, 0
		Rect 564, 456, 62, 12, True
		Color 200, 200, 0
		r# = (Float(MilliSecs() - BulletTime) / (BulletDelay * TankReloadInfluence#)):If r# > 1 Then r# = 1
		Rect 565, 457, 60 * r#, 10, True
		Color 200, 200, 200
		Rect 565, 457, 60, 10, False
	EndIf
	
	;Ping-oval
	Debug("Ping oval...")
	CurPing = PlayerPing:If CurPing > 1000 Then CurPing = 1000
	Color 255 * (Float CurPing / 1000), 255 * (1 - Float CurPing / 1000), 0
	r = 50 * (Float CurPing / 1000)
	Oval GraphicsWidth() - 20 - r, 20 - r, r * 2, r * 2
	
	Debug("Statusbar...")
	If KeyDown(15) Then DrawStatusBar()
	
	Debug("Menu...")
	mh = MouseHit(1)
	If DisplayMenu
		Runtime(MouseX(), MouseY(), MouseXSpeed(), MouseYSpeed(), mh)
		Refresh()
	EndIf
	
	Debug("Flip...")
	Flip VSync
	Debug("calculate FPS...")
	LastFrame = MilliSecs()
	CountFPS()
Wend

Function SendText(command, parameter$)
	If Not LocalID$ = ""
		txt$ = Chr(command) + Chr(Len(parameter$)) + parameter$
		For i = 1 To Len(txt$)
			WriteByte stream, Asc(Mid$(txt$, i, 1))
		Next
		SendUDPMsg Stream, IntIP(CurrentHost$), CurrentPort
	EndIf
End Function

Function CheckStream()
	Debug("Check stream...")
	If Abs(MilliSecs() - MessageTime) > ConnectionProblemDelay
		If Not ConnectionProblemNotified
			AddMessage("Connection problem!", GeneralMessageColor)
			ConnectionProblemNotified = True
		EndIf
	Else
		ConnectionProblemNotified = False
	EndIf
	
	Debug(" * Receive UDP message...")
	from = RecvUDPMsg(Stream)
	If from
		avail = ReadAvail(Stream)
		For i = 1 To avail
			StreamBuffer$ = StreamBuffer$ + Chr$(ReadByte(Stream))
		Next
		; Protocol
		While Len(StreamBuffer) => 3 + Asc(Mid$(StreamBuffer, 3, 1))
			txt$ = Left$(StreamBuffer, 3 + Asc(Mid$(StreamBuffer, 3, 1)))
			StreamBuffer = Mid$(StreamBuffer, Len(txt) + 1)
			CheckMessage(txt$)
		Wend
		
		If Abs(MilliSecs() - MessageTime) > 10000
			CloseUDPStream Stream
			Stream = False
			Notify "Server disconnected!"
			End
		EndIf
	EndIf
End Function

Function CheckMessage(msg$)
	Debug(" * Checking message...")
	Local from$ = Left$(msg$, 1)
	Local action = Asc(Mid$(msg$, 2, 1))
	Local parameter$ = Mid$(msg$, 4, Asc(Mid$(msg$, 3, 1)))
	
	MessageTime = MilliSecs()
	
	For this.player = Each player
		If from$ = this\id$ occ = True
	Next
	If Not LocalID$ = ""
		If from$ = LocalID$ Or from$ = Chr(0) Then occ = True
		If Not occ
			this.player = New player
				this\id$ = from$
				this\team = -1
				this\invisible# = 1
		EndIf
	EndIf
	
	If from$ = Chr(0) ; Server
		Select action
			Case ID_SPAWN
				Respawn()
				PlayerRespawnTime = RespawnDelay
			Case ID_CONNECT
				AddMessage(parameter$ + " joined the game!", GeneralMessageColor)
				Join = PlaySound(JoinSound)
				ChannelVolume Join, GeneralVolume
			Case ID_ID
				If LocalID$ = "" Then
					LocalID$ = Chr(Asc(parameter$))
					SendText(ID_NAME, PlayerName$)
					SendText(ID_TEAM, PlayerTeam)
					SendText(ID_SKIN, PlayerSkin)
				EndIf
			Case ID_TEAM
				PlayerTeam = parameter$
			Case ID_SAY
				AddMessage("Server: " + parameter$, ChatMessageColor)
				Message = PlaySound(MessageSound)
				ChannelVolume Message, GeneralVolume
			Case ID_GAMESPEED
				If Not game_speed = Int(parameter$) Then AddMessage("Game speed changed to " + parameter$, GeneralMessageColor)
				game_speed = parameter$
			Case ID_VIEWDISTANCE
				If Not ViewDistance = Int(parameter$) Then AddMessage("View distance changed to " + parameter$, GeneralMessageColor)
				ViewDistance = parameter$
			Case ID_SPEED
				If Not Speed = parameter$ Then AddMessage("Speed changed to " + parameter$, GeneralMessageColor)
				Speed = parameter$
			Case ID_TURN
				If Not Turn = parameter$ Then AddMessage("Turning speed changed to " + parameter$, GeneralMessageColor)
				Turn = parameter$
			Case ID_BULLETSPEED
				If Not BulletSpeed = parameter$ Then AddMessage("Bullet speed changed to " + parameter$, GeneralMessageColor)
				BulletSpeed = Float(parameter$)
			Case ID_BULLETDELAY
				If Not BulletDelay = parameter$ Then AddMessage("Bullet delay changed to " + parameter$, GeneralMessageColor)
				BulletDelay = Int(parameter$)
			Case ID_RESPAWNDELAY
				If Not RespawnDelay = parameter$ Then AddMessage("Respawn delay changed to " + parameter$, GeneralMessageColor)
				RespawnDelay = Int(parameter$)
				PlayerRespawnTime = RespawnDelay
			Case ID_REALISTICMODE
				If Not RealisticMode = parameter$
					If Sgn(Int(parameter$)) Then
						AddMessage("Realistic mode enabled!", GeneralMessageColor)
					Else
						AddMessage("Realistic mode disabled!", GeneralMessageColor)
					EndIf
				EndIf
				RealisticMode = Sgn(Int(parameter$))
			Case ID_FRAGLIMIT
				If Not FragLimit = parameter$ Then AddMessage("Frag limit changed to " + parameter$, GeneralMessageColor)
				FragLimit = Int(parameter$)
			Case ID_TIMELIMIT
				If Not TimeLimit = parameter$ Then AddMessage("Time limit changed to " + parameter$, GeneralMessageColor)
				TimeLimit = Int(parameter$)
			Case ID_TOPIC
				If Not Topic$ = parameter$ Then AddMessage("Topic: " + parameter$, GeneralMessageColor)
				Topic = parameter$
			Case ID_KICK
				If parameter$ = LocalID$ Or parameter$ = PlayerName$
					SendText(ID_DISCONNECT, "Kicked")
					Notify "You have been kicked from the server!"
					CloseUDPStream Stream
					End
				Else
					AddMessage(parameter$ + " is being kicked!", GeneralMessageColor)
				EndIf
			Case ID_PINGER
				PlayerPing = MessageTime - PingTime
				SendText(ID_PING, PlayerPing)
			Case ID_EXPLODEBULLET
				For bul.bullet = Each bullet
					If bul\id = BytesToShort(parameter$)
						CreateExplosion(bul\x, bul\y)
						Delete bul
					EndIf
				Next
			Case ID_EXPLODEMINE
				For min.mine = Each mine
					If min\id = BytesToShort(parameter$)
						CreateExplosion(min\x, min\y)
						Delete min
					EndIf
				Next
			Case ID_KILLBULLET
				For bul.bullet = Each bullet
					If bul\id = BytesToShort(parameter$) Delete this
				Next
			Case ID_DIE
				If PlayerTeam = PlayerTeam(parameter$) And (Not GameType = 2) Then teamkill$ = " (TEAM KILL)" Else teamkill$ = ""
				AddMessage(parameter$ + " killed you" + teamkill$ + "!", DeathMessageColor)
				PlayerAlive = False
				PlayerRespawnTime = RespawnDelay
				;SendText("Dead", parameter$)
				CreateParticleExplosion(PlayerX, PlayerY, -1, 16, 4, 1)
				CreateParticleExplosion(PlayerX, PlayerY, -1, 16, 4, 2)
				CreateParticleExplosion(PlayerX, PlayerY, -1, 16, 4, 3)
			Case ID_BLUESCORE
				BlueScore = Int(parameter$)
			Case ID_REDSCORE
				RedScore = Int(parameter$)
			Case ID_BLUESCORES
				AddMessage("Estonia scores!", ScoreMessageColor)
				Score = PlaySound(ScoreSound)
				ChannelVolume Score, GeneralVolume
				BlueScore = BlueScore + 1
			Case ID_REDSCORES
				AddMessage("Germany scores!", ScoreMessageColor)
				Score = PlaySound(ScoreSound)
				ChannelVolume Score, GeneralVolume
				RedScore = RedScore + 1
			Case ID_ADMIN
				If Not Int(parameter$) Then
					AddMessage("You are not admin!", GeneralMessageColor)
				Else
					AddMessage("You are an admin!", GeneralMessageColor)
				EndIf
			Case ID_ADMINSTATE
				If parameter$ = PlayerName$
					AddMessage("You granted admin state!", GeneralMessageColor)
				Else
					AddMessage(parameter$ + " granted admin state!", GeneralMessageColor)
				EndIf
			Case ID_FRIENDLYFIRE
				If Not FriendlyFire = Int(parameter$)
					If Sgn(Int(parameter$)) Then
						AddMessage("Friendly fire enabled!", GeneralMessageColor)
					Else
						AddMessage("Friendly fire disabled!", GeneralMessageColor)
					EndIf
				EndIf
				FriendlyFire = Sgn(Int(parameter$))
			Case ID_GAMETYPE
				If Not GameType = Int(parameter$)
					If Int(parameter$) = 0 Then AddMessage("Game type changed to Invasion!", GeneralMessageColor)
					If Int(parameter$) = 1 Then AddMessage("Game type changed to Team Deathmatch!", GeneralMessageColor)
					If Int(parameter$) = 2 Then AddMessage("Game type changed to Deathmatch!", GeneralMessageColor)
				EndIf
				GameType = Int(parameter$)
			Case ID_MOVEMENTSTYLE
				If Not MovementStyle = Int(parameter$)
					If Int(parameter$) = 0 Then AddMessage("Movement style changed to 4 directional!", GeneralMessageColor)
					If Int(parameter$) = 1 Then AddMessage("Movement style changed to 36 directional!", GeneralMessageColor)
				EndIf
				MovementStyle = Int(parameter$)
			Case ID_MAP
				tim = MilliSecs()
				DrawStatusBar()
				SetFont LoadFont("Comic Sans MS", 36, True)
				Color 0, 0, 0
				Text GraphicsWidth() / 2, GraphicsHeight() / 2 - ImageHeight(PlayersListBar) / 2- FontHeight(), "Next map: " + parameter$, True
				Color 250, 250, 250
				Text GraphicsWidth() / 2 + 2, GraphicsHeight() / 2 - ImageHeight(PlayersListBar) / 2- FontHeight() + 2, "Next map: " + parameter$, True
				CopyRect 0, 0, GraphicsWidth(), GraphicsHeight(), 0, 0, BackBuffer(), FrontBuffer()
				Repeat
					Flip
					If Abs(MilliSecs() - tim) > 8000 Exit
				Forever
				ExecFile("be.exe /join be://" + CurrentHost$ + ":" + CurrentPort + "/")
				End
			Case ID_TILE
				x = BytesToShort(Mid$(parameter$, 1, 2))
				y = BytesToShort(Mid$(parameter$, 3, 2))
				tile = BytesToShort(Mid$(parameter$, 5, 2))
				curtile = Map(x, y)
				If curtile = BrickWallIndex Then CreateParticleExplosion(x * TileSize, y * TileSize, Rand(0, 360), 50, 16, 0)
				Map(x, y) = tile
			Case ID_NEWBONUS
				bon.bonus = New bonus
					bon\id = BytesToShort(Mid$(parameter$, 1, 2))
					bon\typ = Asc(Mid$(parameter$, 3, 2))
					bon\x = BytesToShort(Mid$(parameter$, 4, 2))
					bon\y = BytesToShort(Mid$(parameter$, 6, 2))
			Case ID_REMOVEBONUS
				For bon.bonus = Each bonus
					If bon\id = BytesToShort(parameter$) Then Delete bon
				Next
			Default
				If ShowDebug Then AddMessage("Unknown message: ["+ from$ + "] [" + action + "] [" + parameter$ + "]", GeneralMessageColor)
		End Select
	Else ; Client messages-------------------------------------------------------------------------------------------------------------------------------
		Select action
			Case ID_SAY
				If from$ = LocalID$
					AddMessage(PlayerName$ + ": " + parameter$, ChatMessageColor)
					Message = PlaySound(MessageSound)
					ChannelVolume Message, GeneralVolume
				EndIf
				For this.player = Each player
					If from$ = this\id$
						AddMessage(this\name$ + ": " + parameter$, ChatMessageColor)
						Message = PlaySound(MessageSound)
						ChannelVolume Message, GeneralVolume
					EndIf
				Next
			Case ID_TEAMSAY
				If from$ = LocalID$
					AddMessage(PlayerName$ + "(team): " + parameter$, ChatMessageColor)
					Message = PlaySound(MessageSound)
					ChannelVolume Message, GeneralVolume
				EndIf
				For this.player = Each player
					If from$ = this\id$ And PlayerTeam = this\team
						AddMessage(this\name$ + "(team): " + parameter$, ChatMessageColor)
						Message = PlaySound(MessageSound)
						ChannelVolume Message, GeneralVolume
					EndIf
				Next
			Case ID_INFO
				For this.player = Each player
					If from$ = this\id$
						plx = BytesToShort(Mid$(parameter$, 1, 2))
						ply = BytesToShort(Mid$(parameter$, 3, 2))
						plangle = Asc(Mid$(parameter$, 5, 1)) * 10
						
						this\tx = plx
						this\ty = ply
						this\tangle = plangle
						this\distance = GetLenght(this\x, this\y, this\tx, this\ty)
						If this\alive = 0 Then this\alive = True
						Exit
					EndIf
				Next
			Case ID_FRAGS
				If from$ = LocalID$ Then PlayerFrags = Int(parameter$)
				For this.player = Each player
					If from$ = this\id$
						this\frags = Int(parameter$)
						Exit
					EndIf
				Next
			Case ID_DEATHS
				If from$ = LocalID$ Then PlayerDeaths = Int(parameter$)
				For this.player = Each player
					If from$ = this\id$
						this\deaths = Int(parameter$)
						Exit
					EndIf
				Next
			Case ID_NAME
				If from$ = LocalID$
					If (Not PlayerName$ = parameter$) And (Not parameter$ = "") Then AddMessage("Name changed To " + parameter$, GeneralMessageColor)
					PlayerName$ = parameter$
				EndIf
				For this.player = Each player
					If from$ = this\id$
						If Not this\name$ = "" Then AddMessage(this\name$ + " changed name to " + parameter$, GeneralMessageColor)
						this\name$ = parameter$
						Exit
					EndIf
				Next
			Case ID_TEAM
				For this.player = Each player
					If from$ = this\id$
						If parameter$ = 0 Then txt$ = "Germans" Else txt$ = "Estonians"
						If Not this\team < 0 Then AddMessage(this\name$ + " has joined the " + txt$ + "!", GeneralMessageColor)
						this\team = parameter$
						If this\team < 0 Then this\team = 0 ElseIf this\team > 3 Then this\team = 3
						Exit
					EndIf
				Next
			Case ID_SKIN
				For this.player = Each player
					If from$ = this\id$
						this\skin = Int(parameter$)
					EndIf
				Next
			Case ID_PING
				For this.player = Each player
					If from$ = this\id$
						this\ping = parameter$
						Exit
					EndIf
				Next
			Case ID_SHOOT
				x = BytesToShort(Mid$(parameter$, 1, 2))
				y = BytesToShort(Mid$(parameter$, 3, 2))
				angle = Asc(Mid$(parameter$, 5, 1)) * 10
				id = BytesToShort(Mid$(parameter$, 6, 2))
				CreateBullet(from$, x, y, angle, id)
			Case ID_MINE
				x = BytesToShort(Mid$(parameter$, 1, 2))
				y = BytesToShort(Mid$(parameter$, 3, 2))
				id = BytesToShort(Mid$(parameter$, 5, 2))
				CreateMine(from$, x, y, id)
			Case ID_EXPLODEBULLET
				For bul.bullet = Each bullet
					If bul\id = BytesToShort(parameter$)
						CreateExplosion(bul\x, bul\y)
						Delete this
					EndIf
				Next
			Case ID_KILLBULLET
				For bul.bullet = Each bullet
					If bul\id = BytesToShort(parameter$) Delete this
				Next
			Case ID_ALIVE
				If from$ = LocalID$ Then PlayerAlive = Int(parameter)
				For this.player = Each player
					If from$ = this\id$
						this\alive = Int(parameter)
						Exit
					EndIf
				Next
			Case ID_DEAD
				For this.player = Each player
					If from$ = this\id$
						If PlayerName$ = parameter$
							If PlayerTeam = PlayerTeam(this\name$) And (Not GameType = 2) Then teamkill$ = " (TEAM KILL)" Else teamkill$ = ""
							AddMessage("You killed " + this\name$ + teamkill$ + "!", DeathMessageColor)
						Else
							If this\name$ = parameter$
								AddMessage(this\name$ + " commited sucuide!", DeathMessageColor)
							Else
								If PlayerTeam(this\name$) = PlayerTeam(parameter$) And (Not GameType = 2) Then teamkill$ = " (TEAM KILL)" Else teamkill$ = ""
								AddMessage(this\name$ + " has been killed by " + parameter$ + teamkill$ + "!", DeathMessageColor)
							EndIf
						EndIf
						this\alive = 0
						CreateParticleExplosion(this\x, this\y, -1, 16, 4, 1)
						CreateParticleExplosion(this\x, this\y, -1, 16, 4, 2)
						CreateParticleExplosion(this\x, this\y, -1, 16, 4, 3)
					EndIf
				Next
			Case ID_INVISIBLE
				If from$ = LocalID$ Then PlayerInvisibility = Float parameter$
				For this.player = Each player
					If from$ = this\id$
						this\invisible = Float parameter$
					EndIf
				Next
			Case ID_IMMORTALITY
				If from$ = LocalID$ Then PlayerImmortality = Sgn(Int(parameter$))
				For this.player = Each player
					If from$ = this\id$
						this\immortality = Sgn(Int(parameter$))
					EndIf
				Next
			Case ID_RAMBO
				For this.player = Each player
					If from$ = this\id$
						this\rambo = Sgn(Int(parameter$))
					EndIf
				Next
			Case ID_DISCONNECT
				For this.player = Each player
					If from$ = this\id$
						CreateExplosion(this\x, this\y)
						CreateParticleExplosion(this\x, this\y, -1, 16, 4, 1)
						CreateParticleExplosion(this\x, this\y, -1, 16, 4, 2)
						CreateParticleExplosion(this\x, this\y, -1, 16, 4, 3)
						AddMessage(this\name$ + " has left the game! (Reason: " + parameter$ + ")", GeneralMessageColor)
						Delete this
					EndIf
				Next
			Default
				If ShowDebug Then AddMessage("Unknown message: ["+ from$ + "] [" + action + "] [" + parameter$ + "]", GeneralMessageColor)
		End Select
	EndIf
End Function

Function UpdateInfo()
	If Not LocalID$ = ""
		SendText(ID_INFO, ShortToBytes(Int(PlayerX)) + ShortToBytes(Int(PlayerY)) + Chr(Floor(PlayerAngle / 10)) )
		PlayerUpToDate = True
		ResetTimer InfoTimer
	EndIf
End Function

Function SendPingInfo()
	SendText(ID_PINGER, "")
	PingTime = MilliSecs()
End Function

Function CheckCollision(xx#, yy#)
	tile = Map(Floor((PlayerX - ImageWidth(TankImage) / 2.2) / TileSize) + 1, Floor((PlayerY - ImageHeight(TankImage) / 2.2) / TileSize) + 1)
	If (tile => 0 And tile < 24) Or tile = 25 Or (tile => 52 And tile =< 74) Or tile = BrickWallIndex Or tile = SteelWallIndex Or tile = BuildWallIndex
		PlayerX = xx
		PlayerY = yy
		Return
	EndIf
	tile = Map(Floor((PlayerX + ImageWidth(TankImage) / 2.2) / TileSize) + 1, Floor((PlayerY - ImageHeight(TankImage) / 2.2) / TileSize) + 1)
	If (tile => 0 And tile < 24) Or tile = 25 Or (tile => 52 And tile =< 74) Or tile = BrickWallIndex Or tile = SteelWallIndex Or tile = BuildWallIndex
		PlayerX = xx
		PlayerY = yy
		Return
	EndIf
	tile = Map(Floor((PlayerX - ImageWidth(TankImage) / 2.2) / TileSize) + 1, Floor((PlayerY + ImageHeight(TankImage) / 2.2) / TileSize) + 1)
	If (tile => 0 And tile < 24) Or tile = 25 Or (tile => 52 And tile =< 74) Or tile = BrickWallIndex Or tile = SteelWallIndex Or tile = BuildWallIndex
		PlayerX = xx
		PlayerY = yy
		Return
	EndIf
	tile = Map(Floor((PlayerX + ImageWidth(TankImage) / 2.2) / TileSize) + 1, Floor((PlayerY + ImageHeight(TankImage) / 2.2) / TileSize) + 1)
	If (tile => 0 And tile < 24) Or tile = 25 Or (tile => 52 And tile =< 74) Or tile = BrickWallIndex Or tile = SteelWallIndex Or tile = BuildWallIndex
		PlayerX = xx
		PlayerY = yy
		Return
	EndIf
End Function

Function Respawn()
	For t.SpawnPoint = Each Spawnpoint
		If t\team = PlayerTeam Or GameType = 2 Then i = i + 1
	Next
	i = Rand(1, i)
	For t.SpawnPoint = Each Spawnpoint
		If t\team = PlayerTeam Or GameType = 2 Then j = j + 1
		If j = i
			PlayerX = t\x * TileSize - TileSize / 2
			PlayerY = t\y * TileSize - TileSize / 2
			UpdateInfo()
			PlayerAlive = True
			SendText(ID_ALIVE, "")
			RespawnTime = MilliSecs()
			Exit
		EndIf
	Next
End Function

Function Obstacle(x, y, xx, yy)
	If RealisticMode And PlayerAlive
		a = GetAngle(x, y, xx, yy)
		l = GetLenght(x, y, xx, yy)
		For i = 1 To l
			i = i + TileSize
			
			xxx = Floor((x + Cos(a) * i) / TileSize + 1)
			yyy = Floor((y + Sin(a) * i) / TileSize + 1)
			If xxx => 0 And yyy => 0 And xxx <= MapWidth And yyy <= MapHeight Then tile = Map(xxx, yyy)
			If tile = BrickWallIndex Or tile = SteelWallIndex Or tile = BuildWallIndex Return True
		Next
		Return False
	Else
		Return False
	EndIf
End Function

Function ChangeScreenMode()
	Graphics GraphicsWidth(), GraphicsHeight(), GraphicsDepth(), 2 - ScreenMode
	SetFont LoadFont("Courier New", 16)
	MenuFont = LoadFont("System", 16)
	ShowPointer
	MoveMouse GraphicsWidth() / 2, GraphicsHeight() / 2
End Function

Function DrawStatusBar()
	Local count = 0, maxcount, i, j, k, a, temp$
	Local RedCount, BlueCount
	For i = 0 To 100:For j = 0 To 5:PlayerList(i, j) = "":Next:Next
	
	PlayerList(0, 0) = Int(PlayerAlive)
	PlayerList(0, 1) = PlayerName$
	PlayerList(0, 2) = PlayerFrags
	PlayerList(0, 3) = PlayerDeaths
	PlayerList(0, 4) = PlayerTeam
	PlayerList(0, 5) = PlayerSkin
	PlayerList(0, 6) = PlayerPing
	
	For this.player = Each player
		If (Not this\team = -1)
			count = count + 1
			If this\team = 0 Then RedCount = RedCount + 1 Else BlueCount = BlueCount + 1
			PlayerList(count, 0) = Int(this\alive)
			PlayerList(count, 1) = this\name$
			PlayerList(count, 2) = this\frags
			PlayerList(count, 3) = this\deaths
			PlayerList(count, 4) = this\team
			PlayerList(count, 5) = this\skin
			PlayerList(count, 6) = this\ping
		EndIf
	Next
	If RedCount > BlueCount Then maxcount = RedCount Else maxcount = BlueCount
	
	; Sort
	For i = 0 To count
		For j = 0 To count
			If Int(PlayerList(i, 2)) > Int(PlayerList(j, 2))
				For k = 0 To 6
					temp$ = PlayerList(j, k)
					PlayerList(j, k) = PlayerList(i, k)
					PlayerList(i, k) = temp$
				Next
			EndIf
		Next
	Next
	
	Local x = GraphicsWidth() / 2 - 550 / 2
	Local y = GraphicsHeight() / 2 - (FontHeight() * (maxcount + 2)) / 2
	Local h = (FontHeight() * (maxcount + 2)) + 4
	
	; Draw box
	Color 0, 0, 0
	Oval x, y, 10, 10
	Oval x + 540, y, 10, 10
	Oval x + 540, y + h - 10, 10, 10
	Oval x, y + h - 10, 10, 10
	Rect x + 5, y, 540, h
	Rect x, y + 5, 550, h - 10
	
	; Draw header
	Color 250, 250, 250
	DrawImage TeamImages, x + 10, y, 0
	Text x + 30, y, "Name"
	Text x + 130, y, "Frags"
	Text x + 180, y, "Deaths"
	Text x + 235, y, "Ping"
	
	DrawImage TeamImages, x + 275, y, 1
	Text x + 295, y, "Name"
	Text x + 395, y, "Frags"
	Text x + 445, y, "Deaths"
	Text x + 500, y, "Ping"
	
	For i = 0 To count
		If Int(PlayerList(i, 4)) = 0
			a = a + 1
			If (a Mod 2)
				Color (a Mod 2) * 40, (a Mod 2) * 40, (a Mod 2) * 40
				Rect x + 2, y + a * FontHeight(), 545, FontHeight(), True
			EndIf
			If Int(PlayerList(i, 0)) = 0 Then Color 200, 0, 0 Else Color 200, 200, 200
			
			If PlayerList(i, 5) => 0 Then DrawImage SkinImage, x + 10, y + a * FontHeight(), PlayerList(i, 5)
			Text x + 30, y + a * FontHeight(), PlayerList(i, 1)
			Text x + 150, y + a * FontHeight(), PlayerList(i, 2), True
			Text x + 200, y + a * FontHeight(), PlayerList(i, 3), True
			Text x + 250, y + a * FontHeight(), PlayerList(i, 6), True
		EndIf
	Next
	
	a = 0
	For i = 0 To count
		If Int(PlayerList(i, 4)) = 1
			a = a + 1
			If Int(PlayerList(i, 0)) = 0 Then Color 200, 0, 0 Else Color 200, 200, 200
			
			If PlayerList(i, 5) => 0 Then DrawImage SkinImage, x + 275, y + a * FontHeight(), PlayerList(i, 5)
			Text x + 295, y + a * FontHeight(), PlayerList(i, 1)
			Text x + 415, y + a * FontHeight(), PlayerList(i, 2), True
			Text x + 465, y + a * FontHeight(), PlayerList(i, 3), True
			Text x + 515, y + a * FontHeight(), PlayerList(i, 6), True
		EndIf
	Next
End Function

Function LoadMap()
	;Free last map
	For y = 0 To MapWidth
		For x = 0 To MapWidth
			Map(x, y) = False
		Next
	Next
	
	;Load new map
	RecvUDPMsg(Stream)
	avail = ReadAvail(Stream)
	MapName$ = ReadString$(Stream):DebugLog "Map name: " + MapName$
	MapWidth% = ReadShort(Stream):DebugLog "Map width: " + MapWidth
	MapHeight% = ReadShort(Stream):DebugLog "Map height: " + MapHeight
	avail = avail - Len(MapName$) - 4 - 2 * 2
	
	If MapWidth% = 0 And MapHeight% = 0 Then Return False
	
	For y = 1 To MapHeight
		For x = 1 To MapWidth
			If avail <= 0
				RecvUDPMsg(Stream)
				avail = ReadAvail(Stream)
			EndIf
			
			tile = ReadByte(Stream)
			Map(x, y) = tile
			If tile = RedSpawn Or tile = BlueSpawn
				that.SpawnPoint = New SpawnPoint
					that\team = tile - RedSpawn
					that\x = x
					that\y = y
			EndIf
			avail = avail - 1
		Next
	Next
	
	Return True
End Function

Function Message(msg$)
	If Not msg$ = ""
		If Not Left$(msg$, 1) = "/"
			SendText(ID_SAY, msg$)
		Else
			command$ = Mid$(msg$, 2, Instr(msg$, " ") - 2)
			parameter$ = Mid$(msg$, Instr(msg$, " ") + 1)
			Select Lower(command$)
				Case "name"
					If Len(parameter$) =< 12
						SendText(ID_NAME, parameter$)
						ChangeConfigValue("Name", parameter$)
					Else
						AddMessage("Name is too long! Name can only be less then 13 letters!", GeneralMessageColor)
					EndIf
				Case "team"
					If PlayerAlive
						If parameter$ = "1" Or Lower(parameter$) = "estonia"
							AddMessage("Switching team to Estonia!", GeneralMessageColor)
							SendText(ID_TEAM, "1")
							PlayerTeam = 1
						ElseIf parameter$ = "0" Or Lower(parameter$) = "germany"
							AddMessage("Switching team to Germany!", GeneralMessageColor)
							SendText(ID_TEAM, "0")
							PlayerTeam = 0
						EndIf
					Else
						AddMessage("You can't change teams while you are dead!", GeneralMessageColor)
					EndIf
				Case "teamsay", "ts", "t", "tt", "teamtell"
					SendText(ID_TEAMSAY, parameter$)
				Case "smokedelay"
					AddMessage("Smoke delay changed to " + Int(parameter$), GeneralMessageColor)
					SmokeDelay = Int(parameter$)
					ChangeConfigValue("SmokeDelay", Int(parameter$))
				Case "maxparticles"
					AddMessage("Max particles changed to " + Int(parameter$), GeneralMessageColor)
					MaxParticles = Int(parameter$)
					ChangeConfigValue("MaxParticles", Int(parameter$))
				Case "3dwalls"
					ThreeDWalls = Sgn(Int(parameter$))
					If ThreeDWalls Then
						AddMessage("3D walls enabled!", GeneralMessageColor)
					Else
						AddMessage("3D walls disabled!", GeneralMessageColor)
					EndIf
					ChangeConfigValue("3DWalls", Sgn(Int(parameter$)))
				Case "vsync"
					VSync = Sgn(Int(parameter$))
					If VSync Then
						AddMessage("Vertical sync enabled!", GeneralMessageColor)
					Else
						AddMessage("Vertical sync disabled!", GeneralMessageColor)
					EndIf
					ChangeConfigValue("VSync", Sgn(Int(parameter$)))
				Case "adminpassword"
					SendText(ID_ADMINPASSWORD, parameter$)
				Case "topic"
					AddMessage("Topic: " + Topic$, GeneralMessageColor)
				Case "kick"
					SendText(ID_KICK, parameter$)
				Case "gamespeed"
					SendText(ID_GAMESPEED, parameter$)
				Case "viewdistance"
					SendText(ID_VIEWDISTANCE, parameter$)
				Case "speed"
					SendText(ID_SPEED, parameter$)
				Case "turnspeed"
					SendText(ID_TURNSPEED, parameter$)
				Case "bulletspeed"
					SendText(ID_BULLETSPEED, parameter$)
				Case "bulletdelay"
					SendText(ID_BULLETDELAY, parameter$)
				Case "respawndelay"
					SendText(ID_RESPAWNDELAY, parameter$)
				Case "realisticmode"
					SendText(ID_REALISTICMODE, parameter$)
				Case "friendlyfire"
					SendText(ID_FRIENDLYFIRE, parameter$)
				Case "resetmap"
					SendText(ID_RESETMAP, parameter$)
				Case "loadmap"
					SendText(ID_LOADMAP, parameter$)
				Case "fraglimit"
					SendText(ID_FRAGLIMIT, parameter$)
				Case "timelimit"
					SendText(ID_TIMELIMIT, parameter$)
				Case "settopic"
					SendText(ID_SETTOPIC, parameter$)
				Case "addbot"
					SendText(ID_ADDBOT, parameter$)
				Case "exit", "quit"
					SendText(ID_DISCONNECT, "Quit")
					CloseUDPStream Stream
					End
				Default
					AddMessage("Invalid command: " + command$, GeneralMessageColor)
			End Select
		EndIf
	EndIf
End Function

Function UpdateInfluences()
	Select PlayerSkin
		Case 0
			TankSpeedInfluence# = 1
			TankTurnInfluence# = 1
			TankReloadInfluence# = 1
		Case 1
			TankSpeedInfluence# = 1.3
			TankTurnInfluence# = 1.3
			TankReloadInfluence# = 5
		Case 2
			TankSpeedInfluence# = 1
			TankTurnInfluence# = .6
			TankReloadInfluence# = .5
		Case 3
			TankSpeedInfluence# = .5
			TankTurnInfluence# = .9
			TankReloadInfluence# = .6
		Case 4
			TankSpeedInfluence# = .7
			TankTurnInfluence# = .9
			TankReloadInfluence# = 7
	End Select
End Function

Function IntIP(ip$)
	Local a = Sector(ip$, ".", 0)
	Local b = Sector(ip$, ".", 1)
	Local c = Sector(ip$, ".", 2)
	Local d = Sector(ip$, ".", 3)
	Return  (a Shl 24) + (b Shl 16) + (c Shl 8) + d
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

Function PlayerTeam(name$)
	For this.player = Each player
		If this\name$ = name$ Then Return this\team
	Next
	Return -1
End Function

Function Debug(txt$, clear=False)
	If ShowDebug
		If (Not FileType("DebugLog.txt")) Or clear
			file = WriteFile("DebugLog.txt")
			CloseFile file
		EndIf
		file = OpenFile("DebugLog.txt")
		If file
			SeekFile(file, FileSize("DebugLog.txt"))
			WriteLine file, txt$
			CloseFile file
		EndIf
	EndIf
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