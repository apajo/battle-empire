Function MovePlayers()
	For this.connection = Each connection
		a = GetAngle(this\x, this\y, this\tx, this\ty)
		l = GetLenght(this\x, this\y, this\tx, this\ty)
		
		;Calculate prediction
		predict# = Float 1.0 / PackageFrequency
		
		If (l < (GameSpeed / PackageFrequency) * TileSize)
			this\x = this\x + Cos(a) * (this\distance * predict) * multiplier#
			this\y = this\y + Sin(a) * (this\distance * predict) * multiplier#
		Else
			this\x = this\tx
			this\y = this\ty
		EndIf
	Next
End Function

Function MoveBullets()
	For this.bullet = Each bullet
		
		If this\x < 0 Or this\x > MapWidth * TileSize Or this\y < 0 Or this\y > MapHeight * TileSize
			Delete this
		Else
			this\x = this\x + Cos(this\angle) * BulletSpeed * multiplier
			this\y = this\y + Sin(this\angle) * BulletSpeed * multiplier
			
			;Check for collision
			For that.map = Each map
				If that\x = Ceil(this\x / TileSize) And that\y = Ceil(this\y / TileSize)
					If that\tile = BrickWallIndex Or that\tile = SteelWallIndex Or that\tile = BuildWallIndex
						If that\tile = BrickWallIndex Or that\tile = BuildWallIndex
							NewTile = 24
							For cm.constructionmap = Each constructionmap
								If cm\x = that\x And cm\y = that\y
									cm\hp = cm\hp - 1
									HP = cm\hp
									If Hp = 0
										NewTile = cm\tile
										Delete cm
										Exit
									EndIf
								EndIf
							Next
							SendText(Chr(0), ID_EXPLODEBULLET, ShortToBytes(this\timid))
							If HP = 0 Then
								SendText(Chr(0), ID_TILE, ShortToBytes(Int(that\x)) +  ShortToBytes(Int(that\y)) + ShortToBytes(NewTile))
								Map(that\x, that\y) = NewTile
								that\tile = NewTile
							EndIf
						EndIf
						Delete this
						Exit
					ElseIf that\tile = RedEagle
						If (Not ConnectionTeam(this\id$) = 0) And GameType = 0
							SendText(Chr(0), ID_EXPLODEBULLET, this\time)
							SendText(Chr(0), ID_BLUESCORES, "")
							SendText(Chr(0), ID_SPAWN, "")
							BlueScore = BlueScore + 1
							AddMessage("Estonia scores!")
							Delete this
							Return RemoveBullets()
						EndIf
					ElseIf that\tile = BlueEagle
						If (Not ConnectionTeam(this\id$) = 1) And GameType = 0
							SendText(Chr(0), ID_EXPLODEBULLET, this\time)
							SendText(Chr(0), ID_REDSCORES, "")
							SendText(Chr(0), ID_SPAWN, "")
							RedScore = RedScore + 1
							AddMessage("Germany scores!")
							Delete this
							Return RemoveBullets()
						EndIf
					EndIf
				EndIf
			Next
		EndIf
	Next
End Function

Function CheckForDeath()
	For this.bullet = Each bullet
		For that.connection = Each connection
			If that\alive
				If this\time > MilliSecs() Then this\time = this\time - 1000 * 60 * 60 * 24
				a = GetAngle(that\tx, that\ty, that\tx, that\ty)
				
				If GetLenght(that\tx, that\ty, that\tx, that\ty) > 8
					;Calculate prediction
					predict# = Float 1.0 / PackageFrequency
					If Float that\ping / 1000 > predict# Then predict# = Float that\ping / 1000
					
					x = that\x + Cos(a) * (that\distance)
					y = that\y + Sin(a) * (that\distance)
				Else
					x = that\x
					y = that\y
				EndIf
				
				l = GetLenght(x, y, this\x, this\y)
				
				If l < TileSize * .5 Then
					If (Not this\id$ = that\id$) And (((FriendlyFire Or GameType = 2) And ConnectionTeam(this\id$) = ConnectionTeam(that\id$)) Or (Not ConnectionTeam(this\id$) = ConnectionTeam(that\id$)))
						that\alive = that\alive - Rand(10, 30) * (1 - Sgn(that\immortalitytime))
						
						For th.connection = Each connection
							If th\id$ = this\id$
								If that\alive <= 0 Then If (Not that\team = th\team) Or GameType = 2 Then th\frags = th\frags + 1 Else th\frags = th\frags - 1
								AuthorID$ = th\id$
								Frags = th\frags%
								BulletAuthor$ = th\name$
								If th\rambotime > 0 Then that\alive = that\alive - Rand(50, 90) * (1 - Sgn(that\immortalitytime))
							EndIf
						Next
						
						If that\alive <= 0 Then that\alive = 0
						SendText(that\id, ID_ALIVE, that\alive)
						that\deathtime% = MilliSecs()
						If that\alive <= 0 Then
							that\deaths% = that\deaths% + 1
							SendText(AuthorID$, ID_FRAGS, Frags)
							SendText(that\id$, ID_DEATHS, that\deaths)
							If that\ai = True Then
								that\aideathtime = MilliSecs()
								SendText(that\id, ID_DEAD, BulletAuthor$)
							Else
								SendTextToClient(that\intip, that\port, Chr(0), ID_DIE, BulletAuthor$)
								SendText(that\id, ID_DEAD, BulletAuthor$)
							EndIf
							AddMessage(BulletAuthor$ + " killed " + that\name$ + "!")
							that\alive = False
						EndIf
						SendText(Chr(0), ID_EXPLODEBULLET, ShortToBytes(this\timid))
						Delete this
						Exit
					EndIf
				EndIf
			EndIf
		Next
	Next
	
	For min.mine = Each mine
		For that.connection = Each connection
			If Abs(MilliSecs() - min\time) > MineLifeTime
				SendText(Chr(0), ID_EXPLODEMINE, ShortToBytes(min\timid))
				Delete min
				Exit
			EndIf
			
			If that\alive
				l = GetLenght(min\x, min\y, that\x, that\y)
				If l < TileSize * .7
					If (Not min\id$ = that\id$) And (((FriendlyFire Or GameType = 2) And ConnectionTeam(min\id$) = ConnectionTeam(that\id$)) Or (Not ConnectionTeam(min\id$) = ConnectionTeam(that\id$)))
						If Not that\skin = 4
							that\alive = that\alive - Rand(40, 70) * (1 - Sgn(that\immortalitytime))
							
							For th.connection = Each connection
								If th\id$ = min\id$
									If that\alive <= 0 Then If (Not that\team = th\team) Or GameType = 2 Then th\frags = th\frags + 1 Else th\frags = th\frags - 1
									SendText(th\id$, ID_FRAGS, th\frags)
									BulletAuthor$ = th\name$
									AuthorID$ = th\id
									Frags = th\frags
									If th\rambotime > 0 Then that\alive = that\alive - Rand(50, 90) * (1 - Sgn(that\immortalitytime))
								EndIf
							Next
							
							If that\alive < 0 Then that\alive = 0
							SendText(that\id, ID_ALIVE, that\alive)
							that\deathtime% = MilliSecs()
							If that\alive <= 0 Then
								that\deaths% = that\deaths% + 1
								SendText(AuthorID$, ID_FRAGS, Frags)
								SendText(that\id$, ID_DEATHS, that\deaths)
								If that\ai = True
									that\aideathtime = MilliSecs()
									SendText(that\id, ID_DEAD, BulletAuthor$)
								Else
									SendTextToClient(that\intip, that\port, Chr(0), ID_DIE, BulletAuthor$)
									SendText(that\id, ID_DEAD, BulletAuthor$)
								EndIf
								SendText(AuthorID$, ID_FRAGS, Frags)
								AddMessage(BulletAuthor$ + " killed " + that\name$ + "!")
							EndIf
							
							SendText(Chr(0), ID_EXPLODEMINE, ShortToBytes(min\timid))
							Delete min
							Exit
						Else
							SendText(Chr(0), ID_EXPLODEMINE, ShortToBytes(min\timid))
							Delete min
							Exit
						EndIf
					EndIf
				EndIf
			EndIf
		Next
	Next
End Function

Function ConnectionTeam$(id$)
	For this.connection = Each connection
		If this\id$ = id$ Then Return this\team$
	Next
End Function

Function RemoveBullets()
	For this.bullet = Each bullet
		Delete this
	Next
End Function

Function RemoveMines(id$)
	For this.mine = Each mine
		If this\id$ = id$
			SendText(Chr(0), ID_EXPLODEMINE, this\id)
			Delete this
		EndIf
	Next
End Function