Const version$ = "1.08 resurrected"
Global Stream
Global LocalID$ = ""
Global TileSize = 32
Global PackageFrequency = 4
Global ViewDistance = ConfigValue("ViewDistance")
Global MessageBufferSize = Int(ConfigValue("MessageBufferSize")):Dim MessageBuffer$(MessageBufferSize, 2)
Global VSync = ConfigValue("VSync")
Global StreamBuffer$
Global FrameLimit% = ConfigValue("FrameLimit"):Global LastFrame% = MilliSecs()
Global PingDelay = 3000
Global AllowedMessageChars$ = "ABCDEFGHIJKLMNOPQRSZTUVW����XYabcdefghijklmnopqrsztuvw����xy 1234567890+!" + Chr(34) + "#�%&/()[]{}=?-.,:;_-'/*-+<>^ �"
Global ShowDebug = ConfigValue("Debug")
Global WallHeight# = .1
Global DisplayMenu = False
Global ServerName$
Global TeamChangeTimer = MilliSecs()
Global CurrentHost$
Global CurrentPort%
Global MessageTime = MilliSecs()
Global TotalParticles = 0
Global ConnectionProblemDelay = 6000, ConnectionProblemNotified = True

Global GeneralMessageColor$ = ConfigValue("GeneralMessageColor")
Global ChatMessageColor$ = ConfigValue("ChatMessageColor")
Global DeathMessageColor$ = ConfigValue("DeathMessageColor")
Global ScoreMessageColor$ = ConfigValue("ScoreMessageColor")

Global MapMaxHeight% = 300
Global MapMaxWidth% = 300
Dim Map(MapMaxHeight, MapMaxWidth)

Global WaterX# = 0
Global WaterY# = 0
Global WaterAngle# = Rand(0, 360)

Global ExplosionSpeed# = 1
Global ExplosionFrames% = 16
Global ExplosionSoundDistance% = 1000

Global LastSmokeTime = MilliSecs()
Global SmokeSpeed# = 1
Global SmokeDelay% = ConfigValue("SmokeDelay")

Global game_speed% = ConfigValue("GameSpeed")
Global Speed# = ConfigValue("Speed")
Global Turn# = ConfigValue("TurnSpeed")
Global BulletSpeed# = ConfigValue("BulletSpeed")
Global BulletDelay% = ConfigValue("BulletDelay")
Global BulletTime
Global BulletSoundDistance% = 1000
Global MineSoundDistance% = 500
Global RespawnDelay% = ConfigValue("RespawnDelay")
Global RespawnTime
Global TransparentExplosion% = ConfigValue("TransparentExplosion")
Global RealisticMode% = ConfigValue("RealisticMode")
Global ThreeDWalls% = ConfigValue("3DWalls")
Global MaxParticles% = ConfigValue("MaxParticles")
Global GeneralVolume# = Float ConfigValue("GeneralVolume") / 100
Global MusicVolume# = Float ConfigValue("MusicVolume") / 100
Global FriendlyFire = ConfigValue("FriendlyFire")
Global GameType = ConfigValue("GameType")
Global MovementStyle = ConfigValue("MovementStyle")
Global MineLifeTime = ConfigValue("MineLifeTime")
Global FragLimit = ConfigValue("FragLimit")
Global TimeLimit = ConfigValue("TimeLimit")
Global Topic$ = ConfigValue("Topic")

Global Accelrate
Global PlayerAlive = 0
Global PlayerHealth = PlayerAlive
Global PlayerUpToDate = False
Global PlayerName$ = ConfigValue("Name")
Global PlayerTeam = ConfigValue("Team")
Global PlayerFrags = 0
Global PlayerDeaths = 0
Global PlayerX# = -TileSize
Global PlayerY# = -TileSize
Global PlayerAngle# = Rand(0, 359)
Global PlayerSkin = ConfigValue("Tank")
Global PlayerEngineSound
Global PlayerPing
Global PlayerMessage$
Global PlayerRespawnTime = 0
Global PlayerFrame# = 0
Global PlayerImmortality% = 0
Global PlayerImmortalityFrame# = 0
Global Playerinvisibility# = 1
Global PlayerRambo% = 0


Global RedScore
Global BlueScore

Global MapName$
Global MapWidth
Global MapHeight

Global framedelay
Global last_milli% = MilliSecs()
Global multiplier#
Global ScreenMode% = Int(ConfigValue("Fullscreen"))

Global CameraX = 0
Global CameraY = 0

Global fps#
Global fpstime%

Global FrameLimiter = CreateTimer(FrameLimit)
Global InfoTimer = CreateTimer(PackageFrequency)
Global PingTime

Const BrickWallIndex = 100
Const SteelWallIndex = 101
Const BuildWallIndex = 107
Const BushIndex = 102
Const RedSpawn = 103
Const BlueSpawn = 104
Const RedEagle = 105
Const BlueEagle = 106

Global VSyncButton
Global FullScreenButton
Global TransparentExplosionButton
Global ThreeDWallsButton
Global SmokeDelayField
Global MessageBufferSizeField
Global MaxParticlesField

Global OptionsWindow
Global ServerWindow
Global GeneralVolumeSlider
Global MusicVolumeSlider

Global StreamBank = CreateBank(128)


Global web$ = ConfigValue("web")
Global webDir$ = Mid$(web$, Instr(web$, "/"))
web$ = Mid$(web$, 0, Instr(web$, "/"))