import { createApp } from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import { loadFonts } from './plugins/webfontloader'

// // ------- PRETENDER
// import Pretender from "pretender";

// let server = new Pretender();
// // server.get(`${location.origin}/*`, server.passthrough);

// server.get("/goalbox",
//     () => {
//       let response = JSON.stringify([
//           {
//             id: "1",
//             name: "Next trip",
//             ownerAddress: "0xcc4b9091C6DdcdA81536775e76F5759e28aD6F50",
//             contractAddress: "0x14ce9b9864818322f369C63000F344D2e4200F95",
//             balance: 70,
//             goal: 100,
//           },
//           {
//             id: "2",
//             name: "To buy a house",
//             ownerAddress: "0xcc4b9091C6DdcdA81536775e76F5759e28aD6F50",
//             contractAddress: "0x14ce9b9864818322f369C63000F344D2e4200F95",
//             balance: 50,
//             goal: 1000,
//           },
//         ]);

//         return [
//             200, 
//             { "Content-Type": "application/json;charset=UTF-8" }, 
//             response,
//         ];
//     }, 0);

// // ----------------

loadFonts();

createApp(App)
  .use(vuetify)
  .mount('#app');
