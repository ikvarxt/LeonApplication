
export default function encode(str) {
  var encoded = '';
  for (var i = 0; i < str.length; i++) {
    encoded += '%' + ('00' + str.charCodeAt(i).toString(16)).slice(-2);
  }
  return encoded;
}