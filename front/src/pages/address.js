import React, { useInsertionEffect, useState, useEffect } from "react";
import Navbar from "../components/navbar";
// import { useLocalState } from "../util/useLocalStorage";
import APIRequest from "../services/fetch-service";
import { useNavigate, Link, useLocation } from "react-router-dom";
import style from "../styles/index.css";

const Address = () => {
  // const [jwt] = useLocalState("", "jwt");
  const jwt = localStorage.getItem("jwt");
  //const currentUser = JSON.parse(localStorage.getItem("currentUser"));
  //const currentAddress = JSON.parse(localStorage.getItem("currentAddress"));
  const [updatedAddress, setUpdatedAddress] = useState([]);
  //const navigateTo = useNavigate();
  var tempAddress = [];
  const [street, setStreet] = useState("");
  const [postalCode, setPostalCode] = useState("");
  const [city, setCity] = useState("");
  const [country, setCountry] = useState("");
  const location = useLocation();
  const [currentUser, setCurrentUser] = useState({}); // contient l'utilisateur actuellement connecté
  const [currentRole, setCurrentRole] = useState(3);

  console.log("before use effect");
  useEffect(() => {
    setCurrentUser(location.state.currentUser);
    setCurrentRole(location.state.role);
    async function getListAddresses() {
      console.log("in addresses");
      await APIRequest("/address", "GET", jwt).then(async (response) => {
        console.log(response);
        await response.json().then((Addresses) => {
          Addresses.forEach((address) => {
            if (
              address.user.id === currentUser.id &&
              currentUser.role === "ROLE_USER"
            ) {
              setUpdatedAddress((previousState) => [...previousState, address]);
              // setUpdatedAddress([...address]); TO CHECK
            }
            setUpdatedAddress((previousState) => [...previousState, address]);
            //setUpdatedAddress([...address]);
          });
        });
      });
    }
    getListAddresses();
  }, []);
  console.log("after use effect");

  async function CreateAddress() {
    const reqBody = {
      street: street,
      postalCode: postalCode,
      city: city,
      country: country,
    };

    console.log("before request");
    await APIRequest(`/address/`, "POST", jwt, reqBody).then(
      async (response) => {
        if (response.status === 200) {
          await response.json().then((data) => {
            localStorage.setItem("street", data.street);
            localStorage.setItem("postalCode", data.postalCode);
            localStorage.setItem("city", data.city);
            localStorage.setItem("country", data.country);
          });
        }
        //CreateAddress();
      }
    );
    console.log("after request");

    //   async function GetAddress() {
    //     await Request(`/address/`, "GET", jwt).then((response) => {
    //       console.log(response);
    //       if (response.status === 200) {
    //         navigateTo("/");
    //       }
    //     });
    //   }

    //   async function DeleteCurrentAddress() {
    //     await Request(`/address/${currentUser.id}`, "DELETE", jwt).then(
    //       (response) => {
    //         console.log(response);
    //         if (response.status === 200) {
    //           navigateTo("/");
    //         }
    //       }
    //     );
    //   }
  }
  return (
    <>
      <Navbar currentUser={currentUser} role={currentRole} />
      {/* <h4>Addresses: {JSON.stringify(updatedAddress)}</h4> */}
      <div>
        {/* <div className="container-titles"> */}
        <div className="container-titles">
          <h1>ADDRESSES</h1>
        </div>
        {/* </div> */}

        <div className="address-grid">
          {updatedAddress.map((address, index) => {
            const { street, postalCode, city, country } = address;
            return (
              <div className="address-individual">
                <span>
                  <strong>street: </strong>
                </span>{" "}
                <i>{street}</i>
                <br />
                <span>
                  <strong>code postal: </strong>
                </span>{" "}
                <i>{postalCode}</i>
                <br />
                <span>
                  <strong>city: </strong>
                </span>{" "}
                <i>{city}</i>
                <br />
                <span>
                  <strong>country: </strong>
                </span>{" "}
                <i>{country}</i>
                <br />
                <br />
                <div>
                  <Link
                    state={{
                      address: address,
                      currentUser: currentUser,
                      role: currentRole,
                    }}
                    to="/update"
                  >
                    {" "}
                    modify it
                  </Link>
                </div>
              </div>
            );
          })}
        </div>

        {/* {updatedAddress.map((address) => (
          <Link
            state={{
              address: address,
              currentUser: currentUser,
              role: currentRole,
            }}
            to="/update"
          >
            <div>{JSON.stringify(address)}</div>
          </Link>
        ))} */}

        <div className="container-address-form">
          <h4>create an address</h4>
          <label htmlFor="text-form">street</label>
          <br />
          <input
            id="text-form"
            type="text"
            onChange={(event) => {
              setStreet(event.target.value);
            }}
          />
          <br />
          <label htmlFor="text-form">postal code</label>
          <br />
          <input
            id="text-form"
            type="text"
            onChange={(event) => {
              setPostalCode(event.target.value);
            }}
          />
          <br />
          <label htmlFor="text-form">city</label>
          <br />
          <input
            id="text-form"
            type="text"
            onChange={(event) => {
              setCity(event.target.value);
            }}
          />
          <br />
          <label htmlFor="text-form">country</label>
          <br />
          <input
            id="text-form"
            type="text"
            onChange={(event) => {
              setCountry(event.target.value);
            }}
          />
          <br />
          <button
            onClick={async () => {
              await CreateAddress();
              console.log("address created");
            }}
          >
            create address
          </button>
          <br />
          {location.state.fromCart === true ? (
            <Link
              to="/shoppingcart"
              state={{ currentUser: currentUser, role: currentRole }}
            >
              {" "}
              <button>go back to your order</button>
            </Link>
          ) : (
            <></>
          )}
        </div>
      </div>
    </>
  );
};

export default Address;
