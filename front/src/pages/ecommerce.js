import React, { useState, useEffect } from "react";
import Navbar from "../components/navbar";
import { useLocation, Link, useNavigate } from "react-router-dom";
import Artworks from "../components/artworks/artworks";
import { MdShoppingCart } from "react-icons/md";
import { Snackbar, Alert } from "@mui/material";

const Ecommerce = () => {
  const location = useLocation();
  const jwt = localStorage.getItem("jwt");
  const navigateTo = useNavigate();
  const [currentUser, setCurrentUser] = useState({}); // contient l'utilisateur actuellement connecté
  const [currentRole, setCurrentRole] = useState(3);
  const [cartArtworks, setCartArtworks] = useState([]);
  //const countCartArtworks={cartArtworks.length};

  const [isSnackbarVisible, setIsSnackbarVisible] = useState(false);

  const handleCloseSnackbar = () => {
    setIsSnackbarVisible(false);
  };

  const toCart = () => {
    navigateTo(`/shoppingcart`, {
      state: { currentUser: currentUser, role: currentRole },
    });
  };

  useEffect(() => {
    setCurrentUser(location.state.currentUser);
    setCurrentRole(location.state.role);
  }, []);
  return (
    <>
      <Navbar />
      <div>
        <div className="container-titles">
          <h1>SHOP YOUR MEME</h1>
        </div>
        <div className="container-cart">
          <span>
            <i>go to your orders :</i>
          </span>
          <br />
          <button
            className="toCarrito"
            onClick={() => {
              if (localStorage.getItem("cartArtworks") != []) {
                console.log("faut y aller");
                toCart();
                console.log("PASSAGE");
              } else {
                console.log("hop là pas si vite");
                setIsSnackbarVisible(true);
              }
            }}
          >
            <MdShoppingCart className="carrito" />

            {/* {' '}
            {countCartArtworks ? (
              <button className="badge">{countCartArtworks}</button>
            ) : (
              ''
            )} */}
          </button>
        </div>
        <div className="artworks">
          <Artworks />
        </div>
        <div>
          <Snackbar
            open={isSnackbarVisible}
            autoHideDuration={6000}
            onClose={handleCloseSnackbar}
          >
            <Alert severity="error" sx={{ width: "100%" }}>
              your cart is empty
            </Alert>
          </Snackbar>
        </div>
      </div>
    </>
  );
};

export default Ecommerce;
