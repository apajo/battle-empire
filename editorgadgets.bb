Global Toolbar = CreateToolBar("data\graphics\editormenu.bmp", 0, 0, 0, 0, MainWindow)

Global MainPanel = CreatePanel(0, 32, ClientWidth(MainWindow) - 130, ClientHeight(MainWindow) - 35, MainWindow, 1)
Global VScroll = CreateSlider(ClientWidth(MainPanel) - 16, 0, 16, ClientHeight(MainPanel) - 16, MainPanel, 2)
Global HScroll = CreateSlider(0, ClientHeight(MainPanel) - 16, ClientWidth(MainPanel) - 16, 16, MainPanel)

Global tilelabel = CreateLabel("Tile:", ClientWidth(MainWindow) - 125, 5, 50, 13, MainWindow)
Global TileBox = CreateListBox(ClientWidth(MainWindow) - 125, 20, 120, 200, MainWindow)

Global MapNameLabel = CreateLabel("Map name:", ClientWidth(MainWindow) - 125, 225, 100, 14, MainWindow)
Global MapNameField = CreateTextField(ClientWidth(MainWindow) - 125, 240, 120, 20, MainWindow)

Global ShowGrid = CreateButton("Show grid", ClientWidth(MainWindow) - 125, 265, 100, 12, MainWindow, 2)

SetButtonState ShowGrid, 1

SetGadgetIconStrip TileBox, Strip
For i = 0 To TileCount - 1
	AddGadgetItem TileBox, (i + 1), 0, i
Next
SelectGadgetItem TileBox, 0

SetGadgetLayout MainPanel, 2, 1, 1, 2
SetGadgetLayout VScroll, 0, 2, 2, 1
SetGadgetLayout HScroll, 2, 1, 0, 2

SetGadgetLayout TileLabel, 0, 1, 1, 0
SetGadgetLayout Tilebox, 0, 1, 1, 1

SetGadgetLayout MapNameLabel, 0, 1, 0, 01
SetGadgetLayout MapNameField, 0, 1, 0, 1
SetGadgetLayout ShowGrid, 0, 1, 0, 1

DisableGadget VScroll
DisableGadget HScroll