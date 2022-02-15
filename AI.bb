Function CreateBot(parameter$)
	clients = 0
	For this.connection = Each connection
		clients = clients + 1
	Next
	
	If Not Int(TextFieldText(MaxClientsField)) =< Clients
		Local reds%, blues%
		If parameter$ = ""
			For this.connection = Each connection
				If this\team = 0 Then reds = reds + 1 Else blues = blues + 1
			Next
			If reds < blues
				parameter = "0"
			ElseIf reds > blues
				parameter = "1"
			Else
				parameter = Rand(0, 1)
			EndIf
		EndIf
		
		this.connection = New connection
			this\ai = True
			this\team = Sgn(Int(parameter$))
			this\name$ = RandomBotName$()
			this\ping = "BOT"
			this\intip = 0
			this\id$ = Chr(AvailableID())
			this\port = 0
			this\map = True
			this\connected = True
			this\MessageTime = MilliSecs()
			this\skin = Rand(0, 4)
			this\angle = Rand(0, 360)
			
			If Sgn(Int(parameter$)) = 0 Then
				AddMessage("Bot '" + this\name$ + "' added To German side!")
			ElseIf Sgn(Int(parameter$)) = 1
				AddMessage("Bot '" + this\name$ + "' added to Estonian side!")
			EndIf
			
			SendText(this\id$, ID_CONNECT, this\name$)
			SendText(this\id$, ID_NAME, this\name$)
			SendText(this\id$, ID_TEAM, this\team)
			SendText(this\id$, ID_SKIN, this\skin)
			SendText(this\id$, ID_PING, this\ping)
	Else
		AddMessage("Cannot add any bots! Server is full!")
	EndIf
End Function

Function UpdateAI()
	DebugLog("update start...")
	Local x#, y#, collision$
	Local obs1%, obs2%, obs3%
	Local closest#
	For this.connection = Each connection
		If this\ai = True
			If this\alive
				Select this\skin
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
						TankReloadInfluence# = 6
				End Select
			
			
				; Check for enemy
				closest = GraphicsWidth()
				EnemySpotted = False
				For that.connection = Each connection
					If (GameType = 2 Or (Not PlayerTeam(that\name$) = this\team)) And (Not this\id$ = that\id$) And that\alive > 0 And that\invisibletime = 0 And that\immortalitytime = 0
						If Not VisualObstacle(this\tx, this\ty, that\tx, that\ty)
							If GetLenght(this\tx, this\ty, that\x, that\y) < closest
								If that\tx > this\tx - 320 + 50 And that\tx < this\tx + 320 - 50 And that\ty > this\ty - 240 + 50 And that\ty < this\ty + 240 - 50
									closest = GetLenght(this\tx, this\ty, that\x, that\y)
									a# = GetAngle(this\tx, this\ty, that\x, that\y)
									EnemySpotted = True
								EndIf
							EndIf
						EndIf
					EndIf
				Next
				If EnemySpotted
					this\airotate# = Sgn(a - this\angle) * TankTurnInfluence#
					If Abs(a - this\angle) < turn / 2 Then
						this\angle = a
						If this\enemyspottime = 0 Then this\enemyspottime = MilliSecs()
						If MilliSecs() - this\enemyspottime > 500
							If (MilliSecs() - this\aishoottime%) => BulletDelay * TankReloadInfluence#
								If this\skin = 1 And closest < TileSize * 1.5
									AIShoot(2, this\id$, this\x, this\y)
								Else
									AIShoot(1, this\id$, this\x, this\y, this\angle + Rnd(-10, 10))
								EndIf
								this\aishoottime% = MilliSecs()
							EndIf
						EndIf
					Else
						this\enemyspottime = 0
					EndIf
				Else
					If Not Rand(0, 25) Then this\airotate# = Rnd(-TankTurnInfluence#, TankTurnInfluence#)
					If this\angle < 0 Then this\angle = this\angle + 360 ElseIf this\angle > 360 Then this\angle = this\angle - 360
				EndIf
				
				DebugLog("rotate obstacle...")
				
				; Rotate AI
				obs1 = Obstacle(this\tx, this\ty, this\tx + Cos(this\angle) * TileSize, this\ty + Sin(this\angle) * TileSize)
				obs2 = Obstacle(this\tx, this\ty, this\tx + Cos(this\angle - 10) * TileSize, this\ty + Sin(this\angle - 10) * TileSize)
				obs3 = Obstacle(this\tx, this\ty, this\tx + Cos(this\angle + 10) * TileSize, this\ty + Sin(this\angle + 10) * TileSize)
				i = Rand(0, 1)
				try = 0
				DebugLog("rotate obstacle while...")
				While obs1 Or obs2 Or obs3 And try < 32
					try = try + 1
					obs1 = Obstacle(this\tx, this\ty, this\tx + Cos(this\angle) * TileSize, this\ty + Sin(this\angle) * TileSize)
					obs2 = Obstacle(this\tx, this\ty, this\tx + Cos(this\angle - 10) * TileSize, this\ty + Sin(this\angle - 10) * TileSize)
					obs3 = Obstacle(this\tx, this\ty, this\tx + Cos(this\angle + 10) * TileSize, this\ty + Sin(this\angle + 10) * TileSize)
					
					If i = 0 Then this\angle = this\angle + 2 Else this\angle = this\angle - 2
					this\airotate = 0
				Wend
				DebugLog("rotate obstacle end...")
				If this\angle < 0 Then this\angle = this\angle + 360 ElseIf this\angle > 360 Then this\angle = this\angle - 360
				this\angle# = this\angle# + this\airotate# * turn# * multiplier#
				
				; Move AI
				If Not abs1 And obs2 And obs3;try < 32
					this\tx = this\tx + Cos(Ceil(this\angle / 10) * 10) * Speed * TankSpeedInfluence# * multiplier#
					this\ty = this\ty + Sin(Ceil(this\angle / 10) * 10) * Speed * TankSpeedInfluence# * multiplier#
					this\x = this\tx:this\y = this\ty
					;CheckAICollision(this.connection, TankSpeedInfluence#, Speed)
				EndIf
				
				If this\tx < 0 Then this\tx = 0 ElseIf this\tx > MapWidth * TileSize Then this\tx = MapWidth * TileSize
				If this\ty < 0 Then this\ty = 0 ElseIf this\ty > MapWidth * TileSize Then this\ty = MapHeight * TileSize
								
				
			EndIf
			If Abs(MilliSecs() - this\MessageTime) => 1000 / PackageFrequency
				
				If this\alive > 0 Then UploadCertainInfo(this\id$)
				
				this\MessageTime = MilliSecs()
			EndIf
		EndIf
	Next
	DebugLog("update finish")
End Function

Function RespawnAI(id$)
	For this.connection = Each connection
		If this\ai = True And this\id$ = id$
			For that.spawnpoint = Each spawnpoint
				If this\team = that\team Or GameType = 2 Then i = i + 1
			Next
			
			j = Rand(1, i)
			i = 0
			For that.spawnpoint = Each spawnpoint
				If this\team = that\team Or GameType = 2 Then i = i + 1
				If i = j
					this\tx = (that\x + 1) * TileSize - TileSize / 2
					this\ty = (that\y + 1) * TileSize - TileSize / 2
					this\x = this\tx
					this\y = this\ty
					
					this\alive = 100
					
					SendText(this\id$, ID_ALIVE, this\alive)
					UploadCertainInfo(this\id$)
					
					Return True
				EndIf
			Next
		EndIf
	Next
End Function

Function CheckAICollision(this.connection, TankSpeedInfluence#, Speed)
	Local x# = this\tx + Cos(Ceil(this\angle / 10) * 10) * Speed * TankSpeedInfluence# * multiplier# - TileSize / 2
	Local y# = this\ty + Sin(Ceil(this\angle / 10) * 10) * Speed * TankSpeedInfluence# * multiplier# - TileSize / 2
	
	If x > 0 And y > 0 And x < MapWidth * TileSize And y < MapHeight * TileSize
		Local flooring = map(Floor(x / TileSize), y / TileSize) - TileSize / 2
		Local ceiling = map(Ceil(x / TileSize), y / TileSize) - TileSize / 2
		
		If (Not flooring => 0 And flooring < 24) Or (Not flooring = 25) Or (Not flooring => 52 And flooring =< 74) Or (Not flooring = BrickWallIndex)
			If (Not ceiling => 0 And ceiling < 24) Or (Not ceiling = 25) Or (Not ceiling => 52 And ceiling =< 74) Or (Not flooring = BrickWallIndex)
				this\tx = x#
			EndIf
		EndIf
		
		flooring = map(x / TileSize, Floor(y / TileSize)) + TileSize / 2
		ceiling = map(x / TileSize, Ceil(y / TileSize)) + TileSize / 2
		
		If (Not flooring => 0 And flooring < 24) Or (Not flooring = 25) Or (Not flooring => 52 And flooring =< 74) Or (Not flooring = BrickWallIndex)
			If (Not ceiling => 0 And ceiling < 24) Or (Not ceiling = 25) Or (Not ceiling => 52 And ceiling =< 74) Or (Not ceiling = BrickWallIndex)
				this\ty = y#
			EndIf
		EndIf
	EndIf
End Function

Function Obstacle(x, y, xx, yy)
	a = GetAngle(x, y, xx, yy)
	l = GetLenght(x, y, xx, yy)
	For i = 1 To l
		i = i + TileSize
		
		xxx = Floor((x + Cos(a) * i) / TileSize + 1)
		yyy = Floor((y + Sin(a) * i) / TileSize + 1)
		If xxx => 0 And yyy => 0 And xxx <= MapWidth And yyy <= MapHeight Then tile = Map(xxx, yyy)
		If (tile > 0 And tile <= 23) Or tile = 25 Or tile = BrickWallIndex Or tile = SteelWallIndex Or tile = BuildWallIndex Return True
	Next
	Return False
End Function

Function VisualObstacle(x, y, xx, yy)
	If RealisticMode
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

Function AIShoot(weapon%, from$, x, y, angle=0)
	Select weapon
		Case 1
			bul.bullet = New bullet
				bul\id$ = from$
				bul\timid = Right$(MilliSecs(), 5)
				bul\x = x
				bul\y = y
				bul\angle = angle
				bul\time = MilliSecs()
				
			SendText(from$, ID_SHOOT, ShortToBytes(Int(x)) + ShortToBytes(Int(y)) + Chr(Floor(angle / 10)) + ShortToBytes(bul\timid))
		Case 2
			min.mine = New mine
				min\id$ = from$
				min\timid = Right$(MilliSecs(), 5)
				min\x = x
				min\y = y
				min\time = MilliSecs()
				
			SendText(from$, ID_MINE, ShortToBytes(Int(x)) + ShortToBytes(Int(y)) + ShortToBytes(min\timid))
	End Select
End Function

Function PlayerTeam(name$)
	For this.connection = Each connection
		If this\name$ = name$ Then Return this\team
	Next
	Return -1
End Function