import React, { useInsertionEffect, useState, useEffect } from "react";
import Navbar from "../components/navbar";
// import { useLocalState } from "../util/useLocalStorage";
import APIRequest from "../services/fetch-service";
import { useNavigate, Link, useLocation } from "react-router-dom";

const UpdateAndDeleteAddress = () => {
  const jwt = localStorage.getItem("jwt");
  const location = useLocation();
  const address = location.state.address;
  //const currentUser = JSON.parse(localStorage.getItem("currentUser"));
  const prefillStreet = address.street;
  const [currentUser, setCurrentUser] = useState({}); // contient l'utilisateur actuellement connecté
  const [currentRole, setCurrentRole] = useState(3);
  const [street, setStreet] = useState(prefillStreet);

  useEffect(() => {
    setStreet(prefillStreet);
  }, [prefillStreet]);

  const prefillPostalCode = address.postalCode;
  const [postalCode, setPostalCode] = useState(prefillPostalCode);
  useEffect(() => {
    setPostalCode(prefillPostalCode);
  }, [prefillPostalCode]);

  const prefillCity = address.city;
  const [city, setCity] = useState(prefillCity);
  useEffect(() => {
    setCity(prefillCity);
  }, [prefillCity]);

  const prefillCountry = address.country;
  const [country, setCountry] = useState(prefillCountry);
  useEffect(() => {
    setCountry(prefillCountry);
  }, [prefillCountry]);

  useEffect(() => {
    setCurrentUser(location.state.currentUser);
    setCurrentRole(location.state.role);
  }, []);

  console.log(location.state.currentUser);
  console.log(location.state.role);

  async function UpdateAddress() {
    await APIRequest(`/address/${address.id}`, "PUT", jwt, {
      street: street,
      postalCode: postalCode,
      city: city,
      country: country,
    }).then((response) => {
      console.log(response);
      if (response.status === 200) {
        console.log("god send help");
        //navigateTo("/");
      }
    });
  }
  //   UpdateAddress();
  // }, []);

  async function DeleteAddress() {
    await APIRequest(`/address/${address.id}`, "DELETE", jwt, {
      //   street: street,
      //   postalCode: postalCode,
      //   city: city,
      //   country: country,
    }).then((response) => {
      console.log(response);
      if (response.status === 200) {
        console.log("god send help");
        //navigateTo("/");
      }
    });
  }

  return (
    <>
      <Navbar currentUser={currentUser} role={currentRole} />
      <br></br>
      <div className="container-titles">
        <h1>MODIFY ADDRESS</h1>
      </div>
      <div className="container-address-form">
        <label htmlFor="text-form">Street</label>
        <input
          id="text-form"
          type="text"
          defaultValue="Street"
          value={street}
          onChange={(event) => {
            console.log(event.target.value);
            setStreet(event.target.value);
          }}
        />
        <br></br>
        <label htmlFor="text-form">Postal code</label>
        <input
          id="text-form"
          type="text"
          defaultValue="Postal code"
          value={postalCode}
          onChange={(event) => {
            setPostalCode(event.target.value);
          }}
        />
        <br></br>
        <label htmlFor="text-form">City</label>
        <input
          id="text-form"
          type="text"
          defaultValue="City"
          value={city}
          onChange={(event) => {
            setCity(event.target.value);
          }}
        />
        <br></br>
        <label htmlFor="text-form">Country</label>
        <input
          id="text-form"
          type="text"
          defaultValue="Country"
          value={country}
          onChange={(event) => {
            setCountry(event.target.value);
          }}
        />
        <button
          onClick={async () => {
            await UpdateAddress();
            console.log("address updated");
          }}
        >
          Update address
        </button>
        <br />
        <button
          onClick={async () => {
            await DeleteAddress();
            console.log("address deleted");
          }}
        >
          Delete address
        </button>
      </div>
    </>
  );
};

export default UpdateAndDeleteAddress;
