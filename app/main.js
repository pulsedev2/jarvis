const { app, BrowserWindow } = require('electron')

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

  win.setAlwaysOnTop(true)
  win.setMenu(null)

  // and load the index.html of the app.
  win.loadFile('index.html')
}

app.whenReady().then(createWindow)
