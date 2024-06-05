
console.log(`Hello, World!`);
JsViewApi.createView('1', "", 300, 300);

setTimeout(() => {
  JsViewApi.move('1', 100, 100);
  console.log(`move to 100, 100`);
}, 3000);

function main() {
  console.log('call main');
}