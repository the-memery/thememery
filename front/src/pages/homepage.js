import React, { useEffect, useState } from "react";
import Navbar from "../components/navbar";
import APIRequest from "../services/fetchService";
// import tired from "../assets/tired-meme.jpg";
// import style from "../styles/index.css";
// import { Button, Box } from "@mui/material";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
// import Carousel from "react-bootstrap/Carousel";
import ConstructDate from "../util/constructDate";

const Homepage = () => {
  // TODO utiliser des place holder de react-bootstrap

  const [events, setEvents] = useState([]);

  const sliderSettings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
    arrow: true,
  };

  // useEffect is triggered once on page loading (2 fois avant mais j'ai mis en commentaire le <React.StrictMode> du fichier index.js)
  useEffect(() => {
    setup();
  }, []); // si on ne met pas ce tableau de dépendencies vide, le useEffect est rappelé plusieurs fois

  async function setup() {
    // request GET all events
    await APIRequest("/event/byDate", "GET").then(async (response) => {
      await response.json().then((allEvents) => {
        setEvents([...allEvents]);
      });
    });
  }
  return (
    <>
      <Navbar />
      <div className="container">
        <div className="center">
          <p>Life is a meme, take yours.</p>
          <br />
          <br />
          <h3>Upcoming events :</h3>
          {/* <Box width={800}>
            <h3>Upcoming events :</h3>
            <br />
            <div></div>
          </Box> */}
          <br />
          <Slider {...sliderSettings}>
            {events.map((event, index) => {
              const { name, date, image, address } = event;
              return (
                <div key={index} className="carousel-homepage">
                  <br />
                  <img
                    className="img-carousel"
                    src={`data:image/png;base64, ${image}`}
                    height={800}
                    alt=""
                  />
                  <br />
                  <h4>{name}</h4>
                  <label>
                    <i>{ConstructDate(date).toDateString()}</i>
                  </label>
                  <br />
                  <label>
                    {address.street} {address.postalCode} {address.city}
                  </label>
                  <br />
                  <br />
                </div>
              );
            })}
          </Slider>
          {/* <img src={tired} width={300} height={200} /> */}
        </div>
        <br />
        <br />
        <Box width={800}>
          <h3>Upcoming events :</h3>
          <br />
          <div>
            <Slider {...sliderSettings}>
              {events.map((event, index) => {
                const { name, date, image, address } = event;
                return (
                  <div key={index}>
                    <img src={`data:image/png;base64, ${image}`} width={800} />
                    <br />
                    <h4>{name}</h4>
                    <label>
                      <i>{ConstructDate(date).toDateString()}</i>
                    </label>
                    <br />
                    <label>
                      {address.street} {address.postalCode} {address.city}
                    </label>
                    <br />
                    <br />
                  </div>
                );
              })}
            </Slider>
          </div>
        </Box>
      </div>
    </>
  );
};

export default Homepage;
