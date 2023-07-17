import React, { useEffect, useState } from "react";
import Navbar from "../components/navbar";
import Request from "../services/fetch-service";
import GetCurrentUser from "../services/get-current-user";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import ConstructDate from "../util/construct-date";
import { Ellipsis } from 'react-css-spinners';
import "../styles/home.css";

const Home = () => {
  // TODO utiliser des place holder de react-bootstrap

  const [currentUser, setCurrentUser] = useState({}); // contient l'utilisateur actuellement connecté
  const [currentRole, setCurrentRole] = useState(3); // 3 = pas connecté  0 = ROLE_USER  1 = ROLE_ARTIST  2 = ROLE_ADMIN
  const [events, setEvents] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const sliderSettings = {
    className: "slider",
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
    arrow: true,
    autoplay: true,
    autoplaySpeed: 8000,
    cssEase: "linear"
  };

  async function setup() {
    var { currentUser, role } = await GetCurrentUser();
    setCurrentUser(currentUser);
    setCurrentRole(role);

    // request GET all events
    await Request("/event/byDate", "GET").then(async (response) => {
      await response.json().then((allEvents) => {
        setEvents([...allEvents]);
        setIsLoading(false);
      });
    });
  }

  // useEffect is triggered once on page loading (2 fois avant mais j'ai mis en commentaire le <React.StrictMode> du fichier index.js)
  useEffect(() => {
    setup();
  }, []); // si on ne met pas ce tableau de dépendencies vide, le useEffect est rappelé plusieurs fois

  return (
    <>
      <Navbar />
      <div className="home">
        <p>Life is a meme, take yours.</p>
        <h3>Upcoming events :</h3>

        {isLoading ? (
          <div className="placeholder-image">
            <Ellipsis className="circle" color="#E18335" size={130} />
          </div>
        ) : (events.length === 0 ? (
          <div className="placeholder-image">
            <h2>No upcoming events...</h2>
            <h2>Come back later 👅</h2>
          </div>
        ) : (
          <Slider {...sliderSettings} >
            {events.map((event, index) => (
              <div key={index}>
                <img
                  src={`data:image/png;base64, ${event.image}`}
                  alt=""
                />
                <h3>{event.name}</h3>
                <h4>
                  <i>{ConstructDate(event.date).toDateString()}</i>
                </h4>
                <h4>
                  {event.address.street} {event.address.postalCode} {event.address.city}
                </h4>
              </div>
            ))}
          </Slider>
        ))}
      </div>
    </>
  );
};

export default Home;
