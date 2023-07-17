import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import Navbar from "../components/navbar";
import Request from "../services/fetch-service";
import getAllArtworkTechnique from "../util/all-artwork-technique";
import "../styles/artist-profile.css";

const ArtistProfile = () => {
  const [artistArtworks, setArtistArtworks] = useState([]);
  const [firstArtistArtwork, setFirstArtistArtwork] = useState({});
  const location = useLocation();
  const artistToDisplay = location.state.artist;

  async function setup() {
    await Request("/artwork/artist/" + artistToDisplay.id, "GET").then(
      async (response) => {
        await response.json().then((artworks) => {
          setFirstArtistArtwork(artworks[0]);
          artworks.shift();
          setArtistArtworks(artworks);
        });
      }
    );
  }

  useEffect(() => {
    setup();
  }, []);

  return (
    <>
      <Navbar />
      <div className="artist-profile">
        <div className="left">
          <h1>{artistToDisplay.username}</h1>
        </div>

        <div className="right">
          <div className="last-artwork">
            <img item
              className="image"
              src={`data:image/png;base64, ${firstArtistArtwork.image}`}
              style={{ height: 350 }}
            ></img>
            <div item className="text">
              <b className="title">{firstArtistArtwork.title}</b>
              <label className="technique">{getAllArtworkTechnique(firstArtistArtwork.technique)}</label>
            </div>
          </div>

          <div className="grid-container">
            {artistArtworks.map((artwork, index) => {
              const { title, price, technique, image } = artwork;
              return (
                <img
                  src={`data:image/png;base64, ${image}`}
                  className="image"
                ></img>
              );
            })}
          </div>
        </div>
      </div>
    </>
  );

  function FormatPrice(price) {
    return (Math.round(price * 100) / 100).toFixed(2);
  }
};

export default ArtistProfile;
