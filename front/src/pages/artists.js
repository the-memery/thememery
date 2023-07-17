import React, { useEffect, useState } from "react";
import Navbar from "../components/navbar";
import APIRequest from "../services/fetch-service";
import { useNavigate } from "react-router-dom";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import styled from "styled-components";
import ImageListItem from "@mui/material/ImageListItem";
import "../styles/artists.css";

const Artists = () => {
  const [artists, setArtists] = useState([]);
  const navigateTo = useNavigate();
  const [areImagesDisplayed, setAreImagesDisplayed] = useState(false);
  const [img, setImg] = useState([]);

  const toArtistProfile = (selectedArtist) => {
    navigateTo(`/artists/${toURLFormat(selectedArtist.username)}`, {
      state: {
        artist: selectedArtist,
      },
    });
  };

  const toURLFormat = (string) => {
    string = string.toLowerCase();
    return string.replaceAll(" ", "_");
  };

  const getArtworksFromArtist = async (id) => {
    await APIRequest(`/artwork/artist/${id}`, "GET").then(async (response) => {
      await response.json().then((artworks) => {
        console.log(artworks);
        setImg(artworks);
      });
    });
  };

  async function setup() {
    await APIRequest("/user", "GET").then(async (response) => {
      await response.json().then((allUsers) => {
        setArtists([]);
        allUsers.forEach((user) => {
          if (user.role === "ROLE_ARTIST") {
            setArtists((previousState) => [...previousState, user]);
          }
        });
      });
    });
  }

  useEffect(() => {
    setup();
  }, []);

  return (
    <>
      <Navbar />
      <div className="artists">
        <div className="left">
          <div className="grid-artists">
            {artists.map((artist, index) => {
              const { id, username } = artist;
              return (
                <ListItem disablePadding key={index}>
                  <MySpan>
                    <ListItemText
                      style={{ cursor: "pointer", fontFamily: 'Playfair Display, sans-serif' }}
                      primary={username.toUpperCase()}
                      onClick={() => {
                        toArtistProfile(artist);
                      }}
                      onMouseEnter={() => {
                        // afficher ses oeuvres sur la droite
                        // mettre un placeholder le temps du chargement
                        if (!areImagesDisplayed) {
                          getArtworksFromArtist(id);
                        }
                        setAreImagesDisplayed(true);
                      }}
                      onMouseLeave={() => {
                        // cacher ses oeuvres
                        setAreImagesDisplayed(false);
                        setImg([]);
                      }}
                    />
                  </MySpan>
                </ListItem>
              );
            })}
          </div>
        </div>
        <div className="right">
          <div className="grid-artworks">
            {img.map((artwork, index) => (
              <ImageListItem key={index}>
                <img
                  src={`data:image/png;base64, ${artwork.image}`}
                  loading="eager"
                />
              </ImageListItem>
            ))}
          </div>
        </div>
      </div>
    </>
  );
};

const MySpan = styled.span`
  &:hover {
    text-decoration: underline;
  }
`;

export default Artists;
