const { app, BrowserWindow, globalShortcut } = require('electron')


function createWindow () {
  // Cree la fenetre du navigateur.

  let win = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      nodeIntegration: true
    },
    frame: false,
    transparent: true
  })

  //win.setAlwaysOnTop(true)
  win.setMenu(null)
  win.setResizable(false)

  // and load the index.html of the app.
  win.loadFile('index.html')

  globalShortcut.register('f5', function() {
		console.log('f5 is pressed')
		win.reload()
	})

}

app.whenReady().then(createWindow)
