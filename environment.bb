Function Environment()
	; Buttom map
	TileBlock GrassImage, -CameraX, -CameraY
	
	If Not ViewDistance
		xx = Floor(CameraX / TileSize)
		yy = Floor(CameraY / TileSize)
		ww = Floor(GraphicsWidth() / TileSize) + 1
		hh = Floor(GraphicsHeight() / TileSize) + 1
	Else
		xx = Floor(PlayerX / TileSize - ViewDistance)
		yy = Floor(PlayerY / TileSize - ViewDistance)
		ww = Floor(ViewDistance * 2)
		hh = Floor(ViewDistance * 2)
	EndIf
	
	If xx < 1 Then xx = 1
	If yy < 1 Then yy = 1
	If xx + ww > MapWidth Then ww = MapWidth - xx
	If yy + hh > MapHeight Then hh = MapHeight - yy
	
	;Ground map
	For y = yy To yy + hh
		For x = xx To xx + ww
			tile = Map(x, y)
			If tile => 0 And (Not tile > 99) 
				If (Not ViewDistance) Or GetLenght(x, y, PlayerX / TileSize, PlayerY / TileSize) <= ViewDistance
					DrawBlock tiles, (x - 1) * TileSize - CameraX, (y - 1) * TileSize - CameraY, tile
				EndIf
			EndIf
		Next
	Next
	
	;Draw mines
	For min.mine = Each mine
		If (Not ViewDistance) Or GetLenght(PlayerX, PlayerY, min\x, min\y) <= ViewDistance * TileSize
			x = (min\x / TileSize + 1):If x < 0 Then x = 0 ElseIf x > MapWidth Then x = MapWidth
			y = (min\y / TileSize + 1):If y < 0 Then y = 0 ElseIf y > MapWidth Then y = MapWidth
			tile = Map(x, y)
			If tile = 99
				DrawTBMIMage(LandMineImage, min\x - CameraX - TBMImageWidth(LandMineImage) / 6, min\y - CameraY - TBMImageHeight(LandMineImage) / 2, 1, 2, 3)
			ElseIf tile = 50
				DrawTBMIMage(LandMineImage, min\x - CameraX - TBMImageWidth(LandMineImage) / 6, min\y - CameraY - TBMImageHeight(LandMineImage) / 2, 1, 1, 3)
			Else
				DrawTBMIMage(LandMineImage, min\x - CameraX - TBMImageWidth(LandMineImage) / 6, min\y - CameraY - TBMImageHeight(LandMineImage) / 2, 1, 0, 3)
			EndIf
		EndIf
	Next
	
	;Draw particles
	For par.particle = Each particle
		DrawImage ParticleImages(par\particleangle / 10), par\x - CameraX, par\y - CameraY, par\tile
	Next
	
	CheckStream()
	
	Bonus()
	
	;Draw construction area
	If PlayerSkin = 4
		Color 250, 250, 250
		If PlayerAngle > 315 Or PlayerAngle < 135 Then Flooring = False Else Flooring = True
		If Flooring
			Rect Floor(PlayerX / TileSize + Cos(PlayerAngle + 10) * 1.7) * TileSize - CameraX, Floor(PlayerY / TileSize + Sin(PlayerAngle + 10) * 1.7) * TileSize - CameraY, TileSize, TileSize, False
		Else
			Rect Int(PlayerX / TileSize + Cos(PlayerAngle + 10) * 1.7) * TileSize - CameraX, Int(PlayerY / TileSize + Sin(PlayerAngle + 10) * 1.7) * TileSize - CameraY, TileSize, TileSize, False
		EndIf
	EndIf
	
	;Draw players
	For this.player = Each player
		If this\angle < 0 Then this\angle = this\angle + 360 ElseIf this\angle > 359 Then this\angle = this\angle - 360
		If PlayerTeam = this\team Or (Not ViewDistance) Or GetLenght(this\x / TileSize, this\y / TileSize, PlayerX / TileSize, PlayerY / TileSize) <= ViewDistance
			If (PlayerTeam = this\team And (Not GameType = 2)) Or ((Not Obstacle(PlayerX, PlayerY, this\x, this\y)) And this\alive > 0)
				this\visibility# = this\visibility# + multiplier# * .2
			Else 
				this\visibility# = this\visibility# - multiplier# * .2
			EndIf
			If this\visibility# < 0 Then this\visibility# = 0 ElseIf this\visibility# > 1 Then this\visibility# = 1
			
			frame = this\skin * 4 + Floor(this\frame)
			angle = Floor(this\angle / 10) + 1
			team = this\team
			If this\visibility# = 1 And this\invisible# = 1
				DrawImage Tank(Abs(team Mod 2), Abs(angle Mod 36)), this\x - CameraX, this\y - CameraY, Abs(frame Mod 20)
				
				;Draw names
				Color 0, 0, 0
				Text this\x - CameraX, this\y - CameraY - ImageHeight(TankImage), this\name$, 1
				Color 200, 200, 200
				Text this\x - CameraX + 1, this\y - CameraY + 1 - ImageHeight(TankImage), this\name$, 1
			ElseIf this\visibility# > 0
				DrawTransparentImage(Tank(Abs(team Mod 2), Abs(angle Mod 36)), this\x - CameraX, this\y - CameraY, Abs(frame Mod 20), 255, 0, 255, this\visibility# * this\invisible#)
				;DrawImage Tank(Abs(team Mod 2), Abs(angle Mod 36)), this\x - CameraX, this\y - CameraY, Abs(frame Mod 19)
			EndIf
			If this\immortality And this\alive > 0 Then
				DrawTBMImage(ImmortalityImage, this\x - CameraX - TBMImageWidth(ImmortalityImage) / 18, this\y - CameraY - TBMImageHeight(ImmortalityImage) / 2, this\visibility# * this\invisible#, this\immortalityframe%, 9)
				this\immortalityframe% = this\immortalityframe% + multiplier * 2
				If this\immortalityframe% > 8 Then this\immortalityframe% = 0
			EndIf
		EndIf
	Next
	
	If PlayerAngle < 0 Then PlayerAngle = 0 ElseIf PlayerAngle > 359 Then PlayerAngle = PlayerAngle - 360
	If PlayerInvisibility = 1
		If PlayerAlive > 0 DrawImage Tank(2, Floor(PlayerAngle / 10) + 1), PlayerX - CameraX, PlayerY - CameraY, (PlayerSkin * 4 + Floor(PlayerFrame#)) Mod 20
	Else
		If PlayerAlive > 0 Then DrawTransparentImage(Tank(2, Floor(PlayerAngle / 10.0) + 1), PlayerX - CameraX, PlayerY - CameraY, Abs(PlayerFrame Mod 20 - 1), 255, 0, 255, (PlayerInvisibility + 1.0) / 2.0)
	EndIf
	If PlayerImmortality%
		DrawTBMImage(ImmortalityImage, PlayerX - CameraX - TBMImageWidth(ImmortalityImage) / 18, PlayerY - CameraY - TBMImageHeight(ImmortalityImage) / 2, (PlayerInvisibility + 1.0) / 2.0, PlayerImmortalityFrame, 9)
		PlayerImmortalityFrame = PlayerImmortalityFrame + multiplier * 2
		If PlayerImmortalityFrame > 8 Then PlayerImmortalityFrame = 0
	EndIf
	
	;Draw bullets
	For thisbul.bullet = Each bullet
		If (Not ViewDistance) Or GetLenght(PlayerX, PlayerY, thisbul\x, thisbul\y) <= ViewDistance * TileSize
			DrawImage Bullet(thisbul\angle / 10), thisbul\x - CameraX, thisbul\y - CameraY
		EndIf
	Next
	
	;Draw smoke
	For sm.smoke = Each smoke
		If Floor(sm\frame) < sm\frames - 1
			If Not Obstacle(PlayerX, PlayerY, sm\x, sm\y)
				strength# = Float sm\frames / 16
				DrawTBMImage(SmokeImage, sm\x - CameraX - TBMImageWidth(SmokeImage) / 32, sm\y - CameraY - TBMImageHeight(SmokeImage) / 2, strength#, sm\frame, 16)
			EndIf
		Else
			Delete sm
		EndIf
	Next
	
	;Walls
	For y = yy To yy + hh
		For x = xx To xx + ww
			tile = Map(x, y)
			If tile = BrickWallIndex Or tile = SteelWallIndex Or tile = BushIndex Or tile = BuildWallIndex
				If (Not ViewDistance) Or GetLenght(x, y, PlayerX / TileSize, PlayerY / TileSize) <= ViewDistance
					If ThreeDWalls
						If y > 0 Then If Map(x, y - 1) = BrickWallIndex Or Map(x, y - 1) = SteelWallIndex Or Map(x, y - 1) = BushIndex Or Map(x, y - 1) = BuildWallIndex  Then north = False Else north = True
						If y < MapHeight Then If Map(x, y + 1) = BrickWallIndex Or Map(x, y + 1) = SteelWallIndex Or Map(x, y + 1) = BushIndex Or Map(x, y + 1) = BuildWallIndex  Then south = False Else south = True
						If x < MapWidth Then If Map(x + 1, y) = BrickWallIndex Or Map(x + 1, y) = SteelWallIndex Or Map(x + 1, y) = BushIndex Or Map(x + 1, y) = BuildWallIndex  Then east = False Else east = True
						If x > 0 Then If Map(x - 1, y) = BrickWallIndex Or Map(x - 1, y) = SteelWallIndex Or Map(x - 1, y) = BushIndex Or Map(x - 1, y) = BuildWallIndex Then west = False Else west = True
						If tile = BuildWallIndex Then box = 3 Else box = tile - BrickWallIndex
						DrawWall(Floor((x - 1) * TileSize - CameraX), Floor((y - 1) * TileSize - CameraY), box, north, south, east, west)
					Else
						If tile = BuildWallIndex
							DrawImage BuildWall, (x - 1) * TileSize - CameraX, (y - 1) * TileSize - CameraY
						Else
							DrawImage tiles, (x - 1) * TileSize - CameraX, (y - 1) * TileSize - CameraY, tile
						EndIf
					EndIf
				EndIf
			ElseIf tile = RedEagle Or tile = BlueEagle And GameType = 0
				If (Not ViewDistance) Or GetLenght(x, y, PlayerX / TileSize, PlayerY / TileSize) <= ViewDistance
					DrawImage tiles, (x - 1) * TileSize - CameraX, (y - 1) * TileSize - CameraY, tile
				EndIf
			EndIf
		Next
	Next
	
	;Wall roofs
	For y = yy To yy + hh
		For x = xx To xx + ww
			tile = Map(x, y)
			If tile = BrickWallIndex Or tile = SteelWallIndex Or tile = BushIndex Or tile = BuildWallIndex
				If (Not ViewDistance) Or GetLenght(x, y, PlayerX / TileSize, PlayerY / TileSize) <= ViewDistance And ThreeDWalls
					RoofSize = WallCount * WallHeight#
					RoofX = Floor(Floor((x - 1) * TileSize - CameraX) + (Floor((x - 1) * TileSize - CameraX) - GraphicsWidth() / 2) * WallHeight#)
					RoofY = Floor(Floor((y - 1) * TileSize - CameraY) + (Floor((y - 1) * TileSize - CameraY) - GraphicsHeight() / 2) * WallHeight#)
					If tile = BuildWallIndex Then box = 3 Else box = tile - BrickWallIndex
					DrawImage Wall(Float RoofSize + .5), RoofX, RoofY, box
				EndIf
			EndIf
		Next
	Next
	
	;Draw explosions
	For ex.explosion = Each explosion
		If Floor(ex\frame) < ExplosionFrames - 1
			If TransparentExplosion
				DrawTBMImage(TBMExplosionImage, ex\x - CameraX - TBMImageWidth(TBMExplosionImage) / (ExplosionFrames * 2), ex\y - CameraY - TBMImageHeight(TBMExplosionImage) / 2, 1, Floor(ex\frame), ExplosionFrames)
			Else
				DrawImage ExplosionImage, ex\x - CameraX, ex\y - CameraY, Floor(ex\frame)
			EndIf
		Else
			Delete ex
		EndIf
	Next
End Function

Function DrawWall(x, y, frame, north, south, east, west)
	RoofSize = WallCount * WallHeight#
	RoofX = Floor(x + (x - GraphicsWidth() / 2) * WallHeight#)
	RoofY = Floor(y + (y - GraphicsHeight() / 2) * WallHeight#)
	
	;North wall
	If north
		For i = y To RoofY
			flt# = Float (i - y) / (RoofY - y):If flt# < 0 Then flt# = 0 ElseIf flt# > 1 Then flt# = 1
			h = RoofY - y
			w = WallCount + RoofSize * flt#
			xx = (RoofX - x) * flt#
			yy = (i - y)
			DrawImageRect Wall(RoofSize * flt#), x + xx, y + yy, 0, WallCount * flt#, w, 1, frame
		Next
	EndIf
	
	;South wall
	If south
		For i = (RoofY + WallCount) To y + WallCount
			flt# = Float (i - (RoofY + WallCount)) / ((y + WallCount) - (RoofY + WallCount)):If flt# < 0 Then flt# = 0 ElseIf flt# > 1 Then flt# = 1
			h = (y + WallCount) - (RoofY + WallCount)
			w = WallCount + RoofSize * flt#
			xx = (RoofX - x) * flt#
			yy = (i - (RoofY + WallCount))
			DrawImageRect Wall(RoofSize * flt#), x + xx, (y + WallCount) - yy, 0, WallCount * flt#, w, 1, frame
		Next
	EndIf
	
	;East wall
	If east
		For i = (RoofX + WallCount) To x + WallCount
			flt# = Float (i - (RoofX + WallCount)) / ((x + WallCount) - (RoofX + WallCount)):If flt# < 0 Then flt# = 0 ElseIf flt# > 1 Then flt# = 1
			w = (x + WallCount) - (RoofX + WallCount)
			h = WallCount + RoofSize * flt#
			xx = (i - (RoofX + WallCount))
			yy = (RoofY - y) * flt#
			DrawImageRect Wall(RoofSize * flt#), WallCount + x - xx, y + yy, WallCount * flt#, 0, 1, h, frame
		Next
	EndIf
	
	;West wall
	If west
		For i = x To RoofX
			flt# = Float (i - x) / (RoofX - x):If flt# < 0 Then flt# = 0 ElseIf flt# > 1 Then flt# = 1
			w = RoofX - x
			h = WallCount + RoofSize * flt#
			xx = (i - x)
			yy = (RoofY - y) * flt#
			DrawImageRect Wall(RoofSize * flt#), x + xx, y + yy, WallCount * flt#, 0, 1, h, frame
		Next
	EndIf
End Function

Function Bonus()
	For this.bonus = Each bonus
		DrawTBMImage(BonusesImage, this\x - CameraX, this\y - CameraY, 1, this\typ, 5)
	Next
End Function