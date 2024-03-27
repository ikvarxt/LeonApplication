const express = require('express')
const path = require('path')
const range = require('express-range')

const port = 8989
const app = express()

const dir = path.join(__dirname, 'public')

app.use(range())

app.get('/', (req, res) => {
  // raw html download page with a download button
  const files = ['test-file-1'];
  let html = '<h1>Download Center</h1><ul>';

  files.forEach((file) => {
    html += `<li><a href="/download/${file}">${file}</a></li>`;
  });

  html += '</ul>';
  res.send(html);
})

app.get('/download/:file', (req, res) => {
  const file = req.params.file;
  const filePath = path.join(dir, file)
  res.download(filePath, (err) => {
    if (err) {
      console.error(`download error: ${err.message}`)
      res.status(404).send('file not found')
    }
  })
})

app.listen(port)