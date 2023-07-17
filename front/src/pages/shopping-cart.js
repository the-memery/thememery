import React, { useState, useEffect } from "react";
import Navbar from "../components/navbar";
import { useLocation, Link } from "react-router-dom";
import Ecommerce from "./ecommerce.js";
import Artworks from "../components/artworks/artworks";
import { NavItem } from "react-bootstrap";
import "../styles/shopping-cart.css";
//import onAdd from "./ecommerce.js";
//import onRemove from "../components/artworks/artwork.js";
import {
  Button,
  Box,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Snackbar,
  Alert,
} from "@mui/material";
import APIRequest from "../services/fetch-service";

//const ShoppingCart = (props) => {
const ShoppingCart = () => {
  const jwt = localStorage.getItem("jwt");
  // var cartArtwork = [];
  const localStorageValue = localStorage.getItem("cartArtworks");
  // if (localStorage.getItem("cartArtworks") != []) {
  //   cartArtwork = JSON.parse(localStorage.getItem("cartArtworks"));
  // }
  const cartArtwork = localStorageValue ? JSON.parse(localStorageValue) : [];
  console.log(cartArtwork);
  const location = useLocation();
  const [cartArtworks, setCartArtworks] = useState([]);
  const [updatedAddress, setUpdatedAddress] = useState([]);
  const [selectedAddress, setSelectedAddress] = useState("");
  const [currentUser, setCurrentUser] = useState({}); // contient l'utilisateur actuellement connecté
  const [currentRole, setCurrentRole] = useState(3);

  const [isSnackbarVisible, setIsSnackbarVisible] = useState(false);

  const handleCloseSnackbar = () => {
    setIsSnackbarVisible(false);
  };

  useEffect(() => {
    setCurrentUser(location.state.currentUser);
    setCurrentRole(location.state.role);
    setCartArtworks(cartArtwork);
    async function getListAddresses() {
      console.log("in addresses");
      await APIRequest("/address", "GET", jwt).then(async (response) => {
        console.log(response);
        await response.json().then((addresses) => {
          if (addresses.length > 0) {
            addresses.forEach((address) => {
              if (
                address.user.id === currentUser.id &&
                currentUser.role === "ROLE_USER"
              ) {
                // setUpdatedAddress((previousState) => [...previousState, address]);
                setUpdatedAddress([...address]);
              }
              setUpdatedAddress((previousState) => [...previousState, address]);
              //setUpdatedAddress([...address]);
            });
          }
        });
      });
    }
    getListAddresses();
    console.log(updatedAddress);
    console.log("cartArtworks : ", cartArtworks);
  }, []);

  ///////////ON REMOVE///////////
  async function onRemove(artwork) {
    const exist = cartArtwork.find((x) => x.id === artwork.id);
    console.log(cartArtwork, artwork);
    console.log(exist);
    let newCartArtworks;
    if (exist && exist.qty === 1) {
      newCartArtworks = cartArtwork.filter((x) => x.id !== artwork.id);
      //setCartArtworks(newCartArtworks);
      //localStorage.setItem("cartArtworks", JSON.stringify(cartArtworks));
    } else {
      newCartArtworks = cartArtwork.map((x) =>
        x.id === artwork.id ? { ...exist, qty: exist.qty - 1 } : x
      );
      //setCartArtworks(newCartArtworks);
      console.log("else", cartArtworks, artwork, cartArtwork);
    }
    setCartArtworks(newCartArtworks);
    localStorage.setItem("cartArtworks", JSON.stringify(newCartArtworks));
    console.log(exist);
  }
  console.log("magicien");

  function checkOrder() {
    if (cartArtworks != [] && selectedAddress !== "") {
      console.log("ORDER OK");
      setIsSnackbarVisible(true);
      localStorage.setItem("cartArtworks", []);
    } else {
      console.log("ORDER NOT OK");
      console.log(cartArtworks);
      console.log(selectedAddress);
    }
  }

  return (
    <>
      <Navbar currentUser={currentUser} role={currentRole} />
      <div>
        <div className="container-titles">
          <h1>MY CART</h1>
        </div>
        <div className="cart-display">
          {cartArtwork.map((artwork, index) => {
            const { title, price, technique, image } = artwork;
            return (
              <div key={index} className="cart-artwork">
                <div className="cart-artwork-image">
                  <img src={`data:image/jpeg;base64,${image}`} alt={title} />
                </div>
                <div className="cart-artwork-info">
                  <h4> title : {title}</h4>
                  <h4>price : {price} EUR</h4>
                  <h4>technique : {technique}</h4>
                </div>
                <div className="cart-artwork-button-remove">
                  <Button
                    size="small"
                    color="primary"
                    onClick={() => onRemove(artwork)}
                  >
                    remove
                  </Button>
                </div>
              </div>
            );
          })}
        </div>

        <div class="container-address-cart">
          <div>
            <label for="address-select">Select your address:</label>
            <br />
            <br />
            <Box class="Box" sx={{ maxWidth: 400 }}>
              <FormControl fullWidth>
                <InputLabel id="demo-simple-select-label">address</InputLabel>
                <Select
                  labelId="demo-simple-select-label"
                  id="demo-simple-select"
                  label="address"
                  value={selectedAddress}
                  onChange={(event) => {
                    setSelectedAddress(event.target.value);
                  }}
                >
                  {updatedAddress.map((address, index) => {
                    return (
                      <MenuItem key={index} value={index}>
                        {address.street}, {address.city}
                      </MenuItem>
                    );
                  })}
                </Select>
              </FormControl>
            </Box>
          </div>
          <br />
          <div>
            <h4>any addresses yet ?</h4>
          </div>
          <br />
          <div>
            <Link
              to="/profile/address"
              state={{
                currentUser: currentUser,
                role: currentRole,
                fromCart: true,
              }}
            >
              {" "}
              <button>create an address</button>
            </Link>
            <br />
            <button
              onClick={() => {
                checkOrder();
              }}
            >
              validate your order
            </button>
          </div>
        </div>
      </div>

      <div>
        <Snackbar
          open={isSnackbarVisible}
          autoHideDuration={6000}
          onClose={handleCloseSnackbar}
        >
          <Alert severity="success" sx={{ width: "100%" }}>
            your order is confirmed
          </Alert>
        </Snackbar>
      </div>
    </>
  );
};

export default ShoppingCart;
