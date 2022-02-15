img = LoadImage("data\graphics\environment.bmp")
count = ImageWidth(img) / TileSize
FreeImage img
Global tiles = LoadAnimImage("data\graphics\environment.bmp", TileSize, TileSize, 0, count)

Global TankImage = LoadAnimImage("data\graphics\tank.bmp", 32, 32, 0, 20):MaskImage TankImage, 255, 0, 255
Global BulletImage = LoadImage("data\graphics\bullet.bmp")
Global ExplosionImage = LoadAnimImage("data\graphics\explosion.bmp", 71, 100, 0, ExplosionFrames)
Global TBMExplosionImage = LoadTBMImage("data\graphics\explosion.tbm")
Global TeamImages = LoadAnimImage("data\graphics\teams.bmp", 15, 15, 0, 2):MaskImage TeamImages, 255, 255, 255
Global SkinImage = LoadAnimImage("data\graphics\skins.bmp", 15, 15, 0, 5):MaskImage SkinImage, 255, 255, 255
Global ParticleImage = LoadAnimImage("data\graphics\particles.bmp", 16, 16, 0, 4):MaskImage ParticleImage, 255, 0, 255
Global LandMineImage = LoadTBMImage("data\graphics\landmine.tbm")
Global Bar = LoadTBMImage("data\graphics\bar.tbm")
Global GrassImage = LoadImage("data\graphics\grass.bmp")
Global SmokeImage = LoadTBMImage("data\graphics\smoke.tbm");LoadImage("data\graphics\smoke.bmp")
Global BuildWall = LoadImage("data\graphics\buildwall.bmp")

Global BonusesImage = LoadTBMImage("data\graphics\bonuses.tbm")
Global ImmortalityImage = LoadTBMImage("data\graphics\immortality.tbm")

;Generate textures
RedTankTexture = CreateImage(ImageWidth(TankImage), ImageHeight(TankImage))
	buffer = GraphicsBuffer():SetBuffer ImageBuffer(RedTankTexture)
		Color 96, 64, 32:Rect 0, 0, ImageWidth(TankImage), ImageHeight(TankImage)
		For i = 1 To 512
			c = Rand(24, 48)
			Color c * 3, c * 2, c
			x = Rand(0, ImageWidth(TankImage)):y = Rand(0, ImageWidth(TankImage)):w=Rand(1, 8):h=Rand(1, 8)
			Oval x - w / 2, y - h / 2, w, h
		Next
	SetBuffer buffer

BlueTankTexture = CreateImage(ImageWidth(TankImage), ImageHeight(TankImage))
	buffer = GraphicsBuffer():SetBuffer ImageBuffer(BlueTankTexture)
		Color 32, 64, 32:Rect 0, 0, ImageWidth(TankImage), ImageHeight(TankImage)
		For i = 1 To 512
			c = Rand(32, 64)
			Color c, c * 2, c
			x = Rand(0, ImageWidth(TankImage)):y = Rand(0, ImageWidth(TankImage)):w=Rand(1, 8):h=Rand(1, 8)
			Oval x - w / 2, y - h / 2, w, h
		Next
	SetBuffer buffer

PlayerTankTexture = CreateImage(ImageWidth(TankImage), ImageHeight(TankImage))
	buffer = GraphicsBuffer():SetBuffer ImageBuffer(PlayerTankTexture)
		Color 96, 96, 0:Rect 0, 0, ImageWidth(TankImage), ImageHeight(TankImage)
		For i = 1 To 512
			c = Rand(96, 160)
			Color c, c, 0
			x = Rand(0, ImageWidth(TankImage)):y = Rand(0, ImageWidth(TankImage)):w=Rand(1, 8):h=Rand(1, 8)
			Oval x - w / 2, y - h / 2, w, h
		Next
	SetBuffer buffer
	
MaskImage BulletImage, 255, 0, 255
MaskImage ExplosionImage, 255, 0, 255
Global RedTank = CopyImage(TankImage):TextureImage(RedTank, 20, RedTankTexture, 255, 0, 255)
Global BlueTank = CopyImage(TankImage):TextureImage(BlueTank, 20, BlueTankTexture, 255, 0, 255)
Global PlayerTank = CopyImage(TankImage):TextureImage(PlayerTank, 20, PlayerTankTexture, 255, 0, 255)
MidHandle BulletImage
MidHandle ExplosionImage

Dim Tank(2, 36)
	For i = 0 To 36
		Tank(0, i) = CopyImage(RedTank)
		RotateImage Tank(0, i), (i + 9) * 10:MidHandle Tank(0, i)
		Tank(1, i) = CopyImage(BlueTank)
		RotateImage Tank(1, i), (i + 9) * 10:MidHandle Tank(1, i)
		Tank(2, i) = CopyImage(PlayerTank)
		RotateImage Tank(2, i), (i + 9) * 10:MidHandle Tank(2, i)
	Next

Dim Bullet(36)
	For i = 0 To 36
		Bullet(i) = CopyImage(BulletImage)
		RotateImage Bullet(i), i * 10
	Next

tiles3d = CreateImage(ImageWidth(tiles), ImageHeight(tiles), 4, 1)
	buffer = GraphicsBuffer()
		SetBuffer ImageBuffer(tiles, BrickWallIndex):GrabImage tiles3d, 0, 0, 0
		SetBuffer ImageBuffer(tiles, SteelWallIndex):GrabImage tiles3d, 0, 0, 1
		SetBuffer ImageBuffer(tiles, BushIndex):GrabImage tiles3d, 0, 0, 2
		SetBuffer ImageBuffer(BuildWall):GrabImage tiles3d, 0, 0, 3
	SetBuffer buffer
	
Global WallCount = ImageWidth(tiles3d)
Dim Wall(WallCount)
	For i = 0 To WallCount
		Wall(i) = CopyImage(tiles3d)
		TFormImage Wall(i), 1.0 + Float i / WallCount, 0, 0, 1.0 + Float i / WallCount
	Next

Dim ParticleImages(36)
	For i = 0 To 36
		ParticleImages(i) = CopyImage(ParticleImage)
		RotateImage ParticleImages(i), i * 10
		MidHandle ParticleImages(i)
	Next