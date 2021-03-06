LastX# = PlayerX
LastY# = PlayerY
Accelrate = False

Debug(" * Tile effects...")
;Tile Effects
tile = Map((PlayerX / TileSize + .5), (PlayerY / TileSize + .5))
Select tile
	Case 99 ; Sand
		SpeedInfluence# = .6
	Case 50 ; Dirt
		SpeedInfluence# = 1.5
	Case BushIndex
		SpeedInfluence# = 1
		If Not Rand(0, 500) Then
			crow = PlaySound(CrowSound)
			ChannelPitch crow, 8000 + Rand(-1000, 1000)
			ChannelPan crow, Rnd(-1, 1)
			ChannelVolume crow, (.5 + Rnd(0, .5)) * GeneralVolume#
		EndIf
		If Not Rand(0, 500) Then
			crow = PlaySound(OwlSound)
			ChannelPitch crow, 8000 + Rand(-1000, 1000)
			ChannelPan crow, Rnd(-1, 1)
			ChannelVolume crow, (.5 + Rnd(0, .5)) * GeneralVolume#
		EndIf
	Default
		SpeedInfluence# = 1
End Select

Debug(" * Move player...")
If MovementStyle = 1
	If KeyDown(200) And PlayerAlive > 0; Forward
		PlayerX = PlayerX + Cos(Floor(PlayerAngle# / 10 + 1) * 10) * Speed * multiplier * SpeedInfluence * TankSpeedInfluence#
		CheckCollision(LastX, PlayerY)
		PlayerY = PlayerY + Sin(Floor(PlayerAngle# / 10 + 1) * 10) * Speed * multiplier * SpeedInfluence * TankSpeedInfluence#
		CheckCollision(PlayerX, LastY)
		PlayerUpToDate = False
		Accelrate = True
		PlayerFrame# = PlayerFrame# + multiplier * SpeedInfluence * TankSpeedInfluence#
		While Floor(PlayerFrame#) > 3:PlayerFrame# = PlayerFrame# - 4:Wend
	EndIf
	If KeyDown(208) And PlayerAlive > 0 ; Backward
		PlayerX = PlayerX - Cos(Floor(PlayerAngle# / 10 + 1) * 10) * Speed * multiplier * SpeedInfluence * TankSpeedInfluence#
		CheckCollision(LastX, PlayerY)
		PlayerY = PlayerY - Sin(Floor(PlayerAngle# / 10 + 1) * 10) * Speed * multiplier * SpeedInfluence * TankSpeedInfluence#
		CheckCollision(PlayerX, LastY)
		PlayerUpToDate = False
		Accelrate = True
		PlayerFrame# = PlayerFrame# - multiplier * SpeedInfluence * TankSpeedInfluence#
		While Floor(PlayerFrame#) < 0:PlayerFrame# = PlayerFrame# + 4:Wend
	EndIf
	If KeyDown(203) And PlayerAlive > 0
		PlayerAngle# = PlayerAngle# - Turn# * multiplier * TankTurnInfluence#
		PlayerUpToDate = False
		Accelrate = True
	EndIf
	If KeyDown(205) And PlayerAlive > 0
		PlayerAngle# = PlayerAngle# + Turn# * multiplier * TankTurnInfluence#
		PlayerUpToDate = False
		Accelrate = True
	EndIf
	If PlayerAngle > 359 Then PlayerAngle = PlayerAngle - 360 ElseIf PlayerAngle < 0 Then PlayerAngle = PlayerAngle + 360
Else
	If KeyDown(200) And PlayerAlive > 0; Forward
		PlayerAngle = 270 - 10
		PlayerX = PlayerX + Cos(Floor(PlayerAngle# / 10 + 1) * 10) * Speed * multiplier * SpeedInfluence * TankSpeedInfluence#
		CheckCollision(LastX, PlayerY)
		PlayerY = PlayerY + Sin(Floor(PlayerAngle# / 10 + 1) * 10) * Speed * multiplier * SpeedInfluence * TankSpeedInfluence#
		CheckCollision(PlayerX, LastY)
		PlayerUpToDate = False
		Accelrate = True
		PlayerFrame# = PlayerFrame# + multiplier * SpeedInfluence * TankSpeedInfluence#
		While Floor(PlayerFrame#) > 3:PlayerFrame# = PlayerFrame# - 4:Wend
	ElseIf KeyDown(208) And PlayerAlive > 0 ; Backward
		PlayerAngle = 90 - 10
		PlayerX = PlayerX + Cos(Floor(PlayerAngle# / 10 + 1) * 10) * Speed * multiplier * SpeedInfluence * TankSpeedInfluence#
		CheckCollision(LastX, PlayerY)
		PlayerY = PlayerY + Sin(Floor(PlayerAngle# / 10 + 1) * 10) * Speed * multiplier * SpeedInfluence * TankSpeedInfluence#
		CheckCollision(PlayerX, LastY)
		PlayerUpToDate = False
		Accelrate = True
		PlayerFrame# = PlayerFrame# + multiplier * SpeedInfluence * TankSpeedInfluence#
		While Floor(PlayerFrame#) > 3:PlayerFrame# = PlayerFrame# - 4:Wend
	ElseIf KeyDown(203) And PlayerAlive > 0
		PlayerAngle = 180 - 10
		PlayerX = PlayerX + Cos(Floor(PlayerAngle# / 10 + 1) * 10) * Speed * multiplier * SpeedInfluence * TankSpeedInfluence#
		CheckCollision(LastX, PlayerY)
		PlayerY = PlayerY + Sin(Floor(PlayerAngle# / 10 + 1) * 10) * Speed * multiplier * SpeedInfluence * TankSpeedInfluence#
		CheckCollision(PlayerX, LastY)
		PlayerUpToDate = False
		Accelrate = True
		PlayerFrame# = PlayerFrame# + multiplier * SpeedInfluence * TankSpeedInfluence#
		While Floor(PlayerFrame#) > 3:PlayerFrame# = PlayerFrame# - 4:Wend
	ElseIf KeyDown(205) And PlayerAlive > 0
		PlayerAngle = 360 - 10
		PlayerX = PlayerX + Cos(Floor(PlayerAngle# / 10 + 1) * 10) * Speed * multiplier * SpeedInfluence * TankSpeedInfluence#
		CheckCollision(LastX, PlayerY)
		PlayerY = PlayerY + Sin(Floor(PlayerAngle# / 10 + 1) * 10) * Speed * multiplier * SpeedInfluence * TankSpeedInfluence#
		CheckCollision(PlayerX, LastY)
		PlayerUpToDate = False
		Accelrate = True
		PlayerFrame# = PlayerFrame# + multiplier * SpeedInfluence * TankSpeedInfluence#
		While Floor(PlayerFrame#) > 3:PlayerFrame# = PlayerFrame# - 4:Wend
	EndIf
EndIf

Debug(" * Accelration sounds...")
If Accelrate
	ChannelPitch PlayerEngineSound, 22050 + 22050 * SpeedInfluence * TankSpeedInfluence#
Else
	ChannelPitch PlayerEngineSound, 22050
EndIf
ChannelVolume PlayerEngineSound, Float PlayerAlive * GeneralVolume#

Debug(" * Check for shoot...")
;Shoot
If KeyHit(29) And PlayerAlive > 0
	If Abs(MilliSecs() - BulletTime) > BulletDelay * TankReloadInfluence#
		angle = Int(Floor(PlayerAngle / 10) + 1) * 10
		ShootBullet(PlayerX + Cos(angle) * (TileSize / 4), PlayerY + Sin(angle) * (TileSize / 4), angle)
		BulletTime = MilliSecs()
	EndIf
EndIf

Debug(" - Check for secondary weapon...")
;Place a landmine
If KeyHit(42) And PlayerAlive > 0
	If Abs(MilliSecs() - BulletTime) > BulletDelay * TankReloadInfluence#
		Select PlayerSkin
			Case 1
				PlantMine(PlayerX, PlayerY)
				BulletTime = MilliSecs()
			Case 4
				If PlayerAngle > 315 Or PlayerAngle < 135 Then Flooring = False Else Flooring = True
				If Flooring
					SendText(ID_TILE, ShortToBytes(Floor(PlayerX / TileSize + Cos(PlayerAngle + 10) * 1.7) + 1) + ShortToBytes(Floor(PlayerY / TileSize + Sin(PlayerAngle + 10) * 1.7) + 1) + ShortToBytes(BuildWallIndex))
				Else
					SendText(ID_TILE, ShortToBytes((PlayerX / TileSize + Cos(PlayerAngle + 10) * 1.7) + 1) + ShortToBytes((PlayerY / TileSize + Sin(PlayerAngle + 10) * 1.7) + 1) + ShortToBytes(BuildWallIndex))
				EndIf
				BulletTime = MilliSecs()
		End Select
	EndIf
EndIf

Debug(" * Screen mode...")
;Change screen mode
If KeyDown(56) And KeyHit(28)
	ScreenMode = 1 - ScreenMode
	ChangeScreenMode()
EndIf

Debug(" * Chat...")
;Chat
key = GetKey()
If Not DisplayMenu
	If Instr(AllowedMessageChars$, Chr(key))
		If StringWidth(PlayerMessage$) < 530 Then PlayerMessage$ = PlayerMessage$ + Chr(key)
	ElseIf key = 8
		PlayerMessage$ = Left$(PlayerMessage$, Len(PlayerMessage$) - 1)
	ElseIf key = 13
		Message(PlayerMessage$)
		PlayerMessage$ = ""
	EndIf
EndIf

Debug(" * Key bindings...")
;Key Bindings
If key => 63236 And key <= 63247 Then Message(ConfigValue("bind-F" + (key - 63235)))

Debug(" * Menu...")
;Menu
If GuiEventSource
	Select GuiEventSource
		Case HideButton
			DisplayMenu = 1 - DisplayMenu
			GuiEventSource = False
		Case ServerButton
			If Not GuiGadgetExistance(ServerWindow)
				ServerWindow = CreateGuiWindow("Server", GraphicsWidth() / 2 - 240 / 2, GraphicsHeight() / 2 - 230 / 2, 240, 265)
				CreateGuiCloseButton(ServerWindow)
				CreateGuiLabel("Address:", 10, 25, 50, 20, ServerWindow)
				CreateGuiLabel(CurrentHost$ + ":" + CurrentPort, 70, 25, 50, 20, ServerWindow)
				CreateGuiLabel("Name:", 10, 40, 50, 20, ServerWindow)
				CreateGuiLabel(ServerName$, 70, 40, 50, 20, ServerWindow)
				CreateGuiLabel("Map:", 10, 55, 50, 20, ServerWindow)
				CreateGuiLabel(MapName$, 70, 55, 50, 20, ServerWindow)
				
				Select GameType
					Case 0
						CreateGuiLabel("Gamemode: Invasion", 10, 70, 50, 20, ServerWindow)
					Case 1
						CreateGuiLabel("Gamemode: Team deathmatch", 10, 70, 50, 20, ServerWindow)
					Case 2
						CreateGuiLabel("Gamemode: Deathmatch", 10, 70, 50, 20, ServerWindow)
					Case 3
						CreateGuiLabel("Gamemode: Capture the eagle", 10, 70, 50, 20, ServerWindow)
				End Select
				
				CreateGuiPanel("Server options", 10, 105, GuiGadgetWidth(ServerWindow) - 20, 150, ServerWindow)
				CreateGuiLabel("Frag limit: " + FragLimit, 20, 120, 0, 0, ServerWindow)
				CreateGuiLabel("Time limit: " + TimeLimit, 120, 120, 0, 0, ServerWindow)
				CreateGuiLabel("Game speed: " + Game_Speed, 20, 135, 0, 0, ServerWindow)
				CreateGuiLabel("Move speed: " + Speed, 20, 150, 0, 0, ServerWindow)
				CreateGuiLabel("Turn speed: " + Turn, 20, 165, 0, 0, ServerWindow)
				CreateGuiLabel("Bullet speed: " + BulletSpeed, 20, 180, 0, 0, ServerWindow)
				CreateGuiLabel("Bullet delay: " + BulletDelay, 20, 195, 0, 0, ServerWindow)
				CreateGuiLabel("Respawn delay: " + RespawnDelay, 20, 210, 0, 0, ServerWindow)
				CreateGuiLabel("Realistic mode: " + RealisticMode, 20, 225, 0, 0, ServerWindow)
				CreateGuiLabel("Friendly fire: " + FriendlyFire, 20, 240, 0, 0, ServerWindow)
			EndIf
		Case OptionsButton
			If Not GuiGadgetExistance(OptionsWindow)
				OptionsWindow = CreateGuiWindow("Options", GraphicsWidth() / 2 - 190 / 2, GraphicsHeight() / 2 - 180 / 2, 190, 200)
				CreateGuiCloseButton(OptionsWindow)
				VSyncButton = CreateGuiCheckbox("Vsync", 10, 25, 50, 12, OptionsWindow)
				FullscreenButton = CreateGuiCheckbox("Fullscreen", 70, 25, 90, 12, OptionsWindow)
				TransparentExplosionButton = CreateGuiCheckbox("Transparent explosions", 10, 45, 170, 12, OptionsWindow)
				ThreeDWallsButton = CreateGuiCheckbox("3D walls", 10, 65, 90, 12, OptionsWindow)
				
				CreateGuiPanel("Team", 10, 85, 90, 45, OptionsWindow)
				TeamGermanyButton = CreateGuiRadioButton("Germany", 15, 95, 60, 12, OptionsWindow, 1)
				TeamEstoniaButton = CreateGuiRadioButton("Estonia", 15, 110, 60, 12, OptionsWindow, 1)
				
				CreateGuiPanel("Volume", 10, 160, 90, 26, OptionsWindow)
				GeneralVolumeSlider = CreateGuiSlider(15, 170, 80, 6, OptionsWindow)
				CreateGuiPanel("Music", 110, 160, 70, 26, OptionsWindow)
				MusicVolumeSlider = CreateGuiSlider(115, 170, 60, 6, OptionsWindow)
				
				CreateGuiPanel("Tank", 110, 65, 70, 85, OptionsWindow)
				Tank1Button = CreateGuiRadioButton("", 120, 70, 50, 12, OptionsWindow, 2):CreateGuiImage(SkinImage, 135, 68, OptionsWindow, 0)
				Tank2Button = CreateGuiRadioButton("", 120, 85, 50, 12, OptionsWindow, 2):CreateGuiImage(SkinImage, 135, 83, OptionsWindow, 1)
				Tank3Button = CreateGuiRadioButton("", 120, 100, 50, 12, OptionsWindow, 2):CreateGuiImage(SkinImage, 135, 98, OptionsWindow, 2)
				Tank4Button = CreateGuiRadioButton("", 120, 115, 50, 12, OptionsWindow, 2):CreateGuiImage(SkinImage, 135, 113, OptionsWindow, 3)
				Tank5Button = CreateGuiRadioButton("", 120, 130, 50, 12, OptionsWindow, 2):CreateGuiImage(SkinImage, 135, 128, OptionsWindow, 4)
				
				SetGuiGadgetState(VSyncButton, VSync)
				SetGuiGadgetState(FullscreenButton, Screenmode)
				SetGuiGadgetState(TransparentExplosionButton, TransparentExplosion)
				SetGuiGadgetState(ThreeDWallsButton, ThreeDWalls)
				SetGuiGadgetState(GeneralVolumeSlider, GeneralVolume)
				SetGuiGadgetState(MusicVolumeSlider, MusicVolume)
				If Not PlayerTeam
					SetGuiGadgetState(TeamGermanyButton, True):SetGuiGadgetState(TeamEstoniaButton, False)
				Else
					SetGuiGadgetState(TeamEstoniaButton, True):SetGuiGadgetState(TeamGermanyButton, False)
				EndIf
				
				If PlayerSkin = 0 Then SetGuIGadgetState(Tank1Button, True)
				If PlayerSkin = 1 Then SetGuIGadgetState(Tank2Button, True)
				If PlayerSkin = 2 Then SetGuIGadgetState(Tank3Button, True)
				If PlayerSkin = 3 Then SetGuIGadgetState(Tank4Button, True)
				If PlayerSkin = 4 Then SetGuIGadgetState(Tank5Button, True)
			EndIf
		Case VSyncButton
			VSync = GuiGadgetState(VSyncButton)
			ChangeConfigValue("Vsync", VSync)
		Case FullscreenButton
			Screenmode = 1 - Screenmode:ChangeScreenMode()
			ChangeConfigValue("Fullscreen", ScreenMode)
		Case TransparentExplosionButton
			TransparentExplosion = GuiGadgetState(TransparentExplosionButton)
			ChangeConfigValue("TransparentExplosion", TransparentExplosion)
		Case ThreeDWallsButton
			ThreeDWalls = GuiGadgetState(ThreeDWallsButton)
			ChangeConfigValue("3DWalls", ThreeDWalls)
		Case TeamGermanyButton
			If (MilliSecs() - TeamChangeTimer) > 10000
				SendText("Team", "0"):PlayerTeam = 0
				TeamChangeTimer = MilliSecs()
				ChangeConfigValue("Team", "0")
			Else
				AddMessage("You have to wait for " + Int(10 - (MilliSecs() - TeamChangeTimer) / 1000) + " more seconds to switch teams!", GeneralMessageColor)
				If Not PlayerTeam
					SetGuiGadgetState(TeamGermanyButton, True):SetGuiGadgetState(TeamEstoniaButton, False)
				Else
					SetGuiGadgetState(TeamEstoniaButton, True):SetGuiGadgetState(TeamGermanyButton, False)
				EndIf
			EndIf
		Case TeamEstoniaButton
			If (MilliSecs() - TeamChangeTimer) > 10000
				SendText(ID_TEAM, "1"):PlayerTeam = 1
				TeamChangeTimer = MilliSecs()
				ChangeConfigValue("Team", "1")
			Else
				AddMessage("You have to wait for " + Int(10 - (MilliSecs() - TeamChangeTimer) / 1000) + " more seconds to switch teams!", GeneralMessageColor)
				If Not PlayerTeam
					SetGuiGadgetState(TeamGermanyButton, True):SetGuiGadgetState(TeamEstoniaButton, False)
				Else
					SetGuiGadgetState(TeamEstoniaButton, True):SetGuiGadgetState(TeamGermanyButton, False)
				EndIf
			EndIf
		Case Tank1Button, Tank2Button, Tank3Button, Tank4Button, Tank5Button
			If Abs(MilliSecs() - RespawnTime) < 8000
				If GuiGadgetState(Tank1Button) Then SendText(ID_SKIN, "0"):PlayerSkin = 0
				If GuiGadgetState(Tank2Button) Then SendText(ID_SKIN, "1"):PlayerSkin = 1
				If GuiGadgetState(Tank3Button) Then SendText(ID_SKIN, "2"):PlayerSkin = 2
				If GuiGadgetState(Tank4Button) Then SendText(ID_SKIN, "3"):PlayerSkin = 3
				If GuiGadgetState(Tank5Button) Then SendText(ID_SKIN, "4"):PlayerSkin = 4
				UpdateInfluences()
				ChangeConfigValue("Tank", PlayerSkin)
			Else
				AddMessage("You can only change your tank in first 8 seconds after each respawn!", GeneralMessageColor)
			EndIf
		Case GeneralVolumeSlider
			GeneralVolume# = GuigadgetState(GeneralVolumeSlider)
			ChannelVolume BattleSoundChannel, GeneralVolume#
		Case MusicVolumeSlider
			MusicVolume = GuiGadgetState(MusicVolumeSlider)
			ChannelVolume Music, MusicVolume
		Case ExitButton
			SendText(ID_DISCONNECT, "")
			ChangeConfigValue("GeneralVolume", Int(GeneralVolume * 100))
			ChangeConfigValue("MusicVolume", Int(MusicVolume * 100))
			End
	End Select
EndIf


Debug(" * Move players...")
If Abs(MilliSecs() - LastSmokeTime) > SmokeDelay Then AllowSmoke = True Else AllowSmoke = False
For cthis.player = Each player
	a# = GetAngle(cthis\x, cthis\y, cthis\tx, cthis\ty)
	l# = GetLenght(cthis\x, cthis\y, cthis\tx, cthis\ty)
	
	If (l < (game_speed / PackageFrequency) * (TileSize))
		If l > speed * multiplier And cthis\alive > 0
			;Calculate prediction
			predict# = Float 1.0 / PackageFrequency
			
			;Move player
			cthis\x = cthis\x + Float Cos(a) * (cthis\distance * predict) * multiplier
			cthis\y = cthis\y + Float Sin(a) * (cthis\distance * predict) * multiplier
			
			;frames
			cthis\frame# = cthis\frame# + multiplier#
			If Floor(cthis\frame#) > 3 Then cthis\frame# = cthis\frame# - 4
		Else
			cthis\x = cthis\tx
			cthis\y = cthis\ty
		EndIf
	Else
		cthis\x = cthis\tx
		cthis\y = cthis\ty
	EndIf
	
	;Rotate player
	a = cthis\tangle - cthis\angle
	If Abs(a) > 180 Then cthis\angle = cthis\angle + 360 * Sgn(a)
	a = cthis\tangle - cthis\angle
	If Abs(Sgn(a) * Turn# * multiplier) < Abs(cthis\tangle - cthis\angle) Then cthis\angle = cthis\angle + Sgn(a) * Turn# * multiplier Else cthis\angle = cthis\tangle
	If cthis\angle < 0 Then cthis\angle = cthis\angle + 360 ElseIf cthis\angle > 360 Then cthis\angle = cthis\angle - 360
	cthis\angle = cthis\angle Mod 360
	
	If AllowSmoke And (SmokeDelay > 0) And cthis\alive And cthis\invisible# = 1 Then CreateExchaustSmoke(cthis\x - Cos(cthis\angle) * ImageWidth(TankImage) / 2, cthis\y - Sin(cthis\angle) * ImageHeight(TankImage) / 2)
Next
If AllowSmoke And (SmokeDelay > 0) And PlayerAlive > 0 Then CreateExchaustSmoke(PlayerX - Cos(PlayerAngle) * ImageWidth(TankImage) / 2, PlayerY - Sin(PlayerAngle) * ImageHeight(TankImage) / 2)

Debug(" * Move bullets...")
For thisbul.bullet = Each bullet
	thisbul\x = thisbul\x + Cos(thisbul\angle) * BulletSpeed * multiplier
	thisbul\y = thisbul\y + Sin(thisbul\angle) * BulletSpeed * multiplier
	
	If AllowSmoke And (SmokeDelay > 0) Then CreateSmoke(thisbul\x, thisbul\y)
	
	If thisbul\x < 0 Or thisbul\x > MapWidth * TileSize Or thisbul\y < 0 Or thisbul\y > MapHeight * TileSize
		Delete thisbul
	Else
		Select Map(Floor(thisbul\x / TileSize + 1), Floor(thisbul\y / TileSize + 1))
			Case SteelWallIndex
				CreateExplosion(thisbul\x, thisbul\y)
				Delete thisbul
		End Select
	EndIf
Next

Debug(" * Animate...")
For ex.explosion = Each explosion
	ex\frame = ex\frame + ExplosionSpeed# * multiplier#
Next

For sm.smoke = Each smoke
	sm\frame = sm\frame + SmokeSpeed# * multiplier#
Next

Debug(" * Move particles...")
For par.particle = Each particle
	par\particleangle = par\particleangle + par\turn * 10
	If par\particleangle < 0 Then par\particleangle = -par\particleangle Mod 360 ElseIf par\particleangle > 360 Then par\particleangle = par\particleangle Mod 360
	par\turn = par\turn *.9
	par\x = par\x + Cos(par\angle) * multiplier  * Abs(par\turn)
	par\y = par\y + Sin(par\angle) * multiplier * Abs(par\turn)
	
	;If brick is in water
	If par\x < 0 Or par\y < 0 Or par\x > MapWidth * TileSize Or par\y > MapHeight * TileSize
		Delete par
	Else
		tile =  Map(Floor(par\x / TileSize) + 1, Floor(par\y / TileSize) + 1)
		If (tile => 0 And tile < 24) Or tile = 25 Or (tile => 52 And tile =< 75)
			 Delete par
		EndIf
	EndIf
	
	If TotalParticles > MaxParticles
		par.particle = First particle
			Delete par
			TotalParticles = TotalParticles - 1
	EndIf
Next