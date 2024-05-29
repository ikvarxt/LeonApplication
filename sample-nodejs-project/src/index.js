import encode from "./encode.js";

const str = "hello world";
const encoded = encode(str);
console.log(`Encoded string: ${encoded}`);
callNative();

function callNative() {
  nativeCallback();
}