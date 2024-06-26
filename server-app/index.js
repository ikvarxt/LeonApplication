const express = require('express')
const path = require('path')
const fs = require('fs')

const port = 8989
const app = express()

const dir = path.join(__dirname, 'public')

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

app.get('/getFileSize/:file', (req, res) => {
  try {
    const file = req.params.file;
    const filePath = path.join(dir, file)
    const fileSize = fs.statSync(filePath).size;
    res.send(fileSize.toString())
  } catch (e) {
    console.error(`get size error: ${e.message}`)
    res.status(404).send('file not found')
  }
})

app.get('/checkUpdate', (req, res) => {
  return res.json({
    versionName: "1.1",
    versionCode: 2,
    downloadUrl: "http://localhost:8989/download/app-debug-1.2.apk",
    md5: null,
    updateMessage: "test update message",
    forceUpdate: false,
  })
})

app.get('/download/:file', (req, res) => {
  const file = req.params.file;
  const filePath = path.join(dir, file)
  const fileSize = fs.statSync(filePath).size;

  console.log(`######################################### \nstart download: ${fileSize}`)

  // Get the Range header value
  const rangeHeader = req.headers.range;
  console.log(`range header: ${rangeHeader}`)
  const buffer = fs.readFileSync(filePath);

  if (rangeHeader) {
    try {
      const [partialstart, partialend] = rangeHeader.replace(/bytes=/, "").split("-");

      const start = parseInt(partialstart, 10);
      const end = partialend ? parseInt(partialend, 10) : fileSize;
      const chunksize = end - start;
      console.log(`range req start: ${start} end: ${end} chunksize: ${chunksize}`)

      res.send(buffer.subarray(start, end));
    } catch (e) {
      console.error(`range error: ${e.message}`)
      // Invalid Range header
      return res.status(416).send('Range Not Satisfiable');
    }
  } else {
    res.send(buffer)
  }
})

app.listen(port)