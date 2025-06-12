/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


function toggleFavorito(foodtruckId, btn) {
    // Llama a un remoteCommand para actualizar favorito en backend
    toggleFavoritoRC([{name: 'foodtruckId', value: foodtruckId}]);
}