const { app, BrowserWindow, globalShortcut, webContents} = require('electron')



function createWindow () {
    // Cree la fenetre du navigateur.
    let isTerminalOpen = false;

    let win = new BrowserWindow({
        width: 800,
        height: 600,
        webPreferences: {
            nodeIntegration: true
        },
        frame: false,
        transparent: true
    })

    let terminal = new BrowserWindow({
        webPreferences: {
            nodeIntegration: true
        },
        closable: false
    })

    //win.setAlwaysOnTop(true)
    win.setMenu(null)
    win.setResizable(false)
    //terminal.setMenu(null);

    // and load the index.html of the app.
    win.loadFile('html/index.html').then()
    terminal.loadFile('html/terminal.html').then()
    terminal.hide()

    globalShortcut.register('f5', function() {
        console.log('f5 is pressed')
        win.reload()
    })

    globalShortcut.register('f1', function() {
        isTerminalOpen = !isTerminalOpen;
        if(isTerminalOpen){
            terminal.show()
        }else {
            terminal.hide()
        }
    })
}

app.whenReady().then(createWindow)
app.on('window-all-closed', () => {
    app.quit();
});
