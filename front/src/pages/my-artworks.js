import React, { useEffect, useState, useRef, useContext } from "react";
import Navbar from "../components/navbar";
import APIRequest from "../services/fetch-service";
import { useLocation } from "react-router-dom";
import {
  Grid,
  Card,
  Box,
  Stack,
  InputLabel,
  MenuItem,
  FormControl,
  Select,
  Snackbar,
  Alert,
} from "@mui/material";
import getAllArtworkTechnique from "../util/all-artwork-technique";
import { blobToBase64String } from "blob-util";
import Cookies from "js-cookie";
import { UserAuthContext } from "../App";

const MyArtworks = () => {
  const jwt = Cookies.get("jwt");
  const { currentUser, logInHandler, logOutHandler } =
    useContext(UserAuthContext);
  const [currentUserId, setCurrentUserId] = useState(0);
  // const [currentUser, setCurrentUser] = useState({}); // contient l'utilisateur actuellement connecté
  // const [currentRole, setCurrentRole] = useState(3); // 3 = pas connecté  0 = ROLE_USER  1 = ROLE_ARTIST  2 = ROLE_ADMIN
  // const location = useLocation();

  const [myArtworks, setMyArtworks] = useState([]);

  const [artworkTitle, setArtworkTitle] = useState("");
  const [artworkTechnique, setArtworkTechnique] = useState("");
  const [artworkPrice, setArtworkPrice] = useState("");
  const [artworkImage, setArtworkImage] = useState([]);
  const [artworkImageURL, setArtworkImageURL] = useState([]);
  const [artworkImageBase64String, setArtworkImageBase64String] = useState("");

  const [isSnackbarVisible, setIsSnackbarVisible] = useState(false);

  const handleCloseSnackbar = () => {
    setIsSnackbarVisible(false);
  };

  // const getArtworksFromArtist = async () => {

  // };

  // Create a reference to the hidden file input element
  const hiddenFileInput = useRef(null);

  // Programatically click the hidden file input element
  // when the Button component is clicked
  const handleClick = (event) => {
    hiddenFileInput.current.click();
  };

  const setup = async () => {
    var currentUserId = 0;

    await APIRequest("/user", "GET").then(async (response) => {
      await response.json().then((allUsers) => {
        allUsers.forEach((user) => {
          if (user.username === currentUser.username) {
            currentUserId = user.id;
            setCurrentUserId(currentUserId);
          }
        });
      });
    });

    await APIRequest(`/artwork/artist/${currentUserId}`, "GET").then(
      async (response) => {
        if (response.status === 200) {
          await response.json().then((artworks) => {
            setMyArtworks(artworks);
          });
        } else {
          setMyArtworks([]);
        }
      }
    );
  };

  useEffect(() => {
    setup();
  }, []);

  async function processImage() {
    if (artworkImage.length < 1) {
      console.log("artworkImage VIDE");
      return;
    } else {
      console.log("artworkImage REMPLI");
      var binaryData = [];
      artworkImage.forEach(async (image) => {
        binaryData.push(URL.createObjectURL(image));
        setArtworkImageBase64String(await blobToBase64String(image));
      });
      setArtworkImageURL(binaryData);
    }
  }

  useEffect(() => {
    processImage();
  }, [artworkImage]);

  return (
    <>
      <Navbar />

      <div className="container">
        <Stack>
          <h3>Present my artwork</h3>
          <label htmlFor="title">title</label>
          <input
            type="text"
            id="title"
            value={artworkTitle}
            onChange={(event) => {
              setArtworkTitle(event.target.value);
            }}
          />
          <Box sx={{ maxWidth: 200 }}>
            <FormControl fullWidth>
              <InputLabel id="demo-simple-select-label">technique</InputLabel>
              <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                label="technique"
                value={artworkTechnique}
                onChange={(event) => {
                  setArtworkTechnique(event.target.value);
                }}
              >
                {getAllArtworkTechnique("").map((artworkTechnique, index) => {
                  return (
                    <MenuItem key={index} value={index}>
                      {artworkTechnique}
                    </MenuItem>
                  );
                })}
              </Select>
            </FormControl>
          </Box>
          <label htmlFor="price">price</label>
          <input
            type="text"
            id="price"
            value={artworkPrice}
            onChange={(event) => {
              setArtworkPrice(event.target.value);
            }}
          />
          <input
            type="file"
            accept="image/*"
            style={{ display: "none" }} // make the input element invisible
            ref={hiddenFileInput}
            onChange={(event) => {
              setArtworkImage([...event.target.files]);
            }}
          ></input>
          <button onClick={handleClick}>pick image</button>
          {artworkImageURL.map((imageSrc) => (
            <img key={imageSrc} src={imageSrc} style={{ width: 300 }} />
          ))}
          <button
            onClick={async () => {
              await CreateArtwork();
            }}
          >
            add
          </button>
          <Snackbar
            open={isSnackbarVisible}
            autoHideDuration={6000}
            onClose={handleCloseSnackbar}
          // action={action}
          >
            <Alert severity="error" sx={{ width: "100%" }}>
              Missing Field(s)
            </Alert>
          </Snackbar>
        </Stack>
      </div>

      <h2>My Artworks</h2>
      <Grid container spacing={4} justifyContent={"center"}>
        {myArtworks.map((artwork, index) => {
          const { id, title, technique, price, image } = artwork;
          return (
            <div className="container" key={index}>
              <Grid item key={index}>
                <Card style={{ width: 330, height: 440 }}>
                  <Stack spacing={2} style={{ margin: 15 }}>
                    <label>
                      <b>{title}</b>
                    </label>
                    <img
                      src={`data:image/png;base64, ${image}`}
                      style={{ maxWidth: 300, maxHeight: 300 }}
                    ></img>
                    <br />
                    <button
                      style={{ justifySelf: "center" }}
                      onClick={() => {
                        DeleteArtwork(id);
                      }}
                    >
                      delete
                    </button>
                  </Stack>
                </Card>
              </Grid>
            </div>
          );
        })}
      </Grid>
    </>
  );

  async function CreateArtwork() {
    const reqBody = {
      title: artworkTitle,
      technique: artworkTechnique,
      price: artworkPrice,
      image: artworkImageBase64String,
    };

    await APIRequest("/artwork", "POST", jwt, reqBody).then(
      async (response) => {
        if (response.status === 201) {
          setArtworkTitle("");
          setArtworkTechnique("");
          setArtworkPrice("");
          setArtworkImage([]);
          setArtworkImageURL([]);
          setArtworkImageBase64String("");
        } else {
          setIsSnackbarVisible(true);
        }
      }
    );
  }

  async function DeleteArtwork(id) {
    await APIRequest(`/artwork/${id}`, "DELETE", jwt).then(async (response) => {
      if (response.status === 200) {
        console.log("supprimed :)");
        // getArtworksFromArtist(currentUser.id);
        setup();
      }
    });
  }
};

export default MyArtworks;
