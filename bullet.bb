Function ShootBullet(x, y, angle)
	SendText(ID_SHOOT, ShortToBytes(Int(x)) + ShortToBytes(Int(y)) + Chr(Floor(angle / 10)))
End Function

Function PlantMine(x, y)
	SendText(ID_MINE, ShortToBytes(Int(x)) + ShortToBytes(Int(y)))
End Function

Function CreateBullet(from$, x#, y#, angle, id)
	Shot = PlaySound(ShotSound)
	ChannelPan Shot, Cos(GetAngle(PlayerX, PlayerY, x, y) - (PlayerAngle + 90))
	l = GetLenght(PlayerX, PlayerY, x, y)
	ChannelVolume Shot, (1.0 - (Float l / BulletSoundDistance)) * GeneralVolume#
	thisbul.bullet = New bullet
		thisbul\ip$ = from$
		thisbul\x = x#
		thisbul\y = y#
		thisbul\angle = angle
		thisbul\id = id
End Function

Function CreateMine(from$, x#, y#, id)
	Mine = PlaySound(MineSound)
	ChannelPan Mine, Cos(GetAngle(PlayerX, PlayerY, x, y) - (PlayerAngle + 90))
	l = GetLenght(PlayerX, PlayerY, x, y)
	ChannelVolume Mine, (1.0 - (Float l / MineSoundDistance)) * GeneralVolume#
	thismine.mine = New mine
		thismine\ip$ = from$
		thismine\x = x#
		thismine\y = y#
		thismine\id = id
End Function

Function CreateExplosion(x, y)
	Hit = PlaySound(HitSound)
	ChannelPan Hit, Cos(GetAngle(PlayerX, PlayerY, x, y) - (PlayerAngle + 90))
	l = GetLenght(PlayerX, PlayerY, x, y)
	If Not l Then l = 1
	ChannelVolume Hit, (1.0 - (Float l / ExplosionSoundDistance)) * GeneralVolume#
	this.explosion = New explosion
		this\frame# = 0
		this\x = x
		this\y = y
End Function

Function CreateSmoke(x, y)
	this.smoke = New smoke
		this\frame = 1
		this\frames = 16
		this\x = x
		this\y = y
		LastSmokeTime = MilliSecs()
End Function

Function CreateExchaustSmoke(x, y)
	this.smoke = New smoke
		this\frame = 1
		this\frames = 8
		this\x = x
		this\y = y
		LastSmokeTime = MilliSecs()
End Function

Function CreateParticleExplosion(x, y, angle, strength, amount, tile)
	For i = 1 To amount
		TotalParticles = TotalParticles + 1
		this.particle = New particle
			this\x = x
			this\y = y
			If angle => 0 Then this\angle = angle + Rand(-45, 45) Else this\angle = Rand(0, 360)
			this\particleangle = Rand(0, 360)
			this\turn = Rand(-strength, strength)
			this\tile = tile
	Next
End Function