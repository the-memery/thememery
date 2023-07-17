import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import APIRequest from "../../services/fetch-service";
import {
  Button,
  CardActionArea,
  CardActions,
  Chip,
  Stack,
  Typography,
  CardMedia,
  CardContent,
  Card,
} from "@mui/material";

//const Artworks = (props) => {
const Artworks = () => {
  //const { addItem } = useCart();
  const jwt = localStorage.getItem("jwt");
  const [artworks, setArtworks] = useState([]);
  const [cartArtworks, setCartArtworks] = useState([]);
  // const [carrito, setCarrito] = useState([]);
  var carritoParsed;
  if (localStorage.getItem("cartArtworks") != []) {
    carritoParsed = JSON.parse(localStorage.getItem("cartArtworks"));
  }

  if (carritoParsed == null) {
    carritoParsed = [];
  }
  const location = useLocation();
  const [currentUser, setCurrentUser] = useState({}); // contient l'utilisateur actuellement connecté
  const [currentRole, setCurrentRole] = useState(3);

  useEffect(() => {
    setCurrentUser(location.state.currentUser);
    setCurrentRole(location.state.role);
    //setCartArtworks(carritoParsed);
    async function getListArtworks() {
      await APIRequest("/artwork", "GET").then(async (response) => {
        // console.log("response from /artwork" + response);
        await response.json().then((Artworks) => {
          if (Artworks.length > 0) {
            console.log("voilà les oeuvres : " + Artworks);
            setArtworks([...Artworks]);
          }
        });
      });
    }
    getListArtworks();
    localStorage.setItem("cartArtworks", JSON.stringify(cartArtworks));
  }, [cartArtworks]);
  console.log("after use effect");

  console.log("before onAdd");

  /////////////////////////ON ADD/////////////////////////
  async function onAdd(artwork) {
    console.log("inside onAdd");
    const exist = cartArtworks.find((x) => x.id === artwork.id);
    console.log(exist);
    console.log(cartArtworks);
    if (exist) {
      setCartArtworks(
        cartArtworks.map((x) =>
          x.id === artwork.id ? { ...exist, qty: exist.qty + 1 } : x
        )
      );
    } else {
      setCartArtworks([...cartArtworks, { ...artwork, qty: 1 }]);
    }
    console.log(exist);
  }
  console.log("after onAdd");
  console.log("debug", cartArtworks);

  return (
    <>
      <div>
        <div class="cards-grid">
          {artworks.map((artwork, index) => {
            const { title, price, technique, image } = artwork;
            return (
              <div key={index} class="card-individual">
                <Card>
                  <CardActionArea>
                    <CardMedia
                      component="img"
                      style={{ height: "100%", objectFit: "cover" }}
                      image={`data:image/png;base64, ${image}`}
                      alt="green iguana"
                    />
                    <CardContent>
                      <Typography gutterBottom variant="h5" component="div">
                        {title}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        technique: {technique}
                        <Stack sx={{ mt: 2 }} direction="row" spacing={1}>
                          <Chip label={price + " €"} />
                        </Stack>
                      </Typography>
                    </CardContent>
                  </CardActionArea>
                  <CardActions>
                    <Button
                      size="small"
                      color="primary"
                      onClick={() => onAdd(artwork)}
                      className="addToCartButton"
                    >
                      add to cart
                    </Button>
                  </CardActions>
                </Card>
              </div>
            );
          })}
        </div>
      </div>
    </>
  );
};

export default Artworks;
