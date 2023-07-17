import React, { useEffect, useState, useRef } from "react";
import PropTypes from "prop-types";
import {
  InputLabel,
  MenuItem,
  FormControl,
  Select,
  Stack,
  Dialog,
  DialogTitle,
  Box,
} from "@mui/material";
import getAllEventType from "../util/all-event-type";
import { Button } from "react-bootstrap";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import { blobToBase64String } from "blob-util";
import Grid from "@mui/material/Unstable_Grid2";
import APIRequest from "../services/fetch-service";
import ConstructDate from "../util/construct-date";

const SimpleDialog = (props) => {
  const jwt = localStorage.getItem("jwt");
  const { open, currentUser, setEvents, event, onClose } = props;

  const [eventName, setEventName] = useState("");
  const [eventType, setEventType] = useState("");
  const [eventDate, setEventDate] = useState(new Date());
  const [eventImage, setEventImage] = useState([]);
  const [eventImageURL, setEventImageURL] = useState([]);
  const [eventImageBase64String, setEventImageBase64String] = useState("");
  const [eventStreet, setEventStreet] = useState("");
  const [eventPostalCode, setEventPostalCode] = useState("");
  const [eventCity, setEventCity] = useState("");
  const [eventCountry, setEventCountry] = useState("");

  // const setEveryField = async () => {
  //   setEventDate(event.date);
  //   setEventStreet(event.address.street);
  //   setEventPostalCode(event.address.postalCode);
  //   setEventCity(event.address.city);
  //   setEventCountry(event.address.country);
  // };

  // Create a reference to the hidden file input element
  const hiddenFileInput = useRef(null);

  // Programatically click the hidden file input element
  // when the Button component is clicked
  const handleClick = (event) => {
    hiddenFileInput.current.click();
  };

  const handleCloseDialog = () => {
    onClose();
  };

  const handleCreate = async () => {
    const reqBodyAddress = {
      street: eventStreet,
      postalCode: eventPostalCode,
      city: eventCity,
      country: eventCountry,
      user: currentUser,
    };

    await APIRequest("/address", "POST", jwt, reqBodyAddress).then(
      async (response) => {
        if (response.status === 201) {
          await response.json().then(async (newAddress) => {
            await CreateEvent(jwt, newAddress);
            onClose();
          });
        }
      }
    );
  };

  async function CreateEvent(jwt, bodyNewAddress) {
    const reqBodyEvent = {
      name: eventName,
      type: eventType,
      date: eventDate,
      image: eventImageBase64String,
      address: bodyNewAddress,
    };

    await APIRequest("/event", "POST", jwt, reqBodyEvent).then(
      async (response) => {
        if (response.status === 201) {
          setEventImageURL([]);
        } else {
          response.json().then((data) => {
            console.log("la data = " + data);
          });
        }
      }
    );
  }

  useEffect(() => {
    async function processImage() {
      // if (!isCreation) {
      // await setEveryField();
      // }

      if (eventImage.length < 1) {
        return;
      } else {
        var binaryData = [];
        eventImage.forEach(async (image) => {
          binaryData.push(URL.createObjectURL(image));
          setEventImageBase64String(await blobToBase64String(image));
        });
        setEventImageURL(binaryData);
      }
    }
    processImage();
  }, [event, eventImage]);

  return (
    <>
      <Dialog
        onClose={handleCloseDialog}
        open={open}
        fullWidth
        style={{ margin: 10 }}
      >
        <DialogTitle style={{ alignSelf: "center", fontFamily: 'Playfair Display' }}>
          create new event
        </DialogTitle>
        <Grid container sx={{ pt: 0 }} spacing={2} maxWidth={608} padding={2}>
          <Grid xs={6}>
            <Stack spacing={2}>
              <input
                type="text"
                placeholder="name"
                style={{ minHeight: 55, maxWidth: 260 }}
                // value={event.name}
                onChange={(event) => {
                  setEventName(event.target.value);
                }}
              ></input>
              <input
                type="file"
                accept="image/*"
                style={{ display: 'none' }} // make the input element invisible
                ref={hiddenFileInput}
                onChange={(event) => {
                  setEventImage([...event.target.files]);
                }}
              ></input>
              <button onClick={handleClick}>pick image</button>
              {eventImageURL.map((imageSrc) => (
                <img key={imageSrc} src={imageSrc} maxWidth={300} />
              ))}
              {/* { event.address == undefined ? (eventImageURL.map((imageSrc) => (
                <img key={imageSrc} src={imageSrc} maxWidth={300} alt={"test"} />
              ))) : <img src={`data:image/png;base64, ${event.image}`} height={236} />} */}
              <input
                type="text"
                placeholder="street"
                // value={eventStreet}
                style={{ maxWidth: 260, marginLeft: 6 }}
                onChange={(event) => {
                  setEventStreet(event.target.value);
                }}
              ></input>
              <input
                type="text"
                placeholder="city"
                style={{ maxWidth: 260, marginLeft: 6 }}
                // value={eventCity}
                onChange={(event) => {
                  setEventCity(event.target.value);
                }}
              ></input>
            </Stack>
          </Grid>
          {/* <img src={`data:image/png;base64, ${image}`} width={300}></img> */}
          <Grid xs={6}>
            <Stack spacing={2}>
              <FormControl style={{ minWidth: 200 }}>
                <InputLabel id="event-type">type</InputLabel>
                <Select
                  labelId="event-type"
                  label="type"
                  // defaultValue={getAllEventType(event.type)}
                  onChange={(event) => {
                    setEventType(event.target.value);
                  }}
                >
                  {getAllEventType("").map((eventType, index) => {
                    return (
                      <MenuItem key={index} value={index}>
                        {eventType}
                      </MenuItem>
                    );
                  })}
                </Select>
              </FormControl>
              <Box>
                <Calendar
                  defaultValue={ConstructDate(event.date)}
                  // value={eventDate}
                  onChange={setEventDate}
                ></Calendar>
              </Box>
              <input
                type="text"
                placeholder="postal code"
                style={{ maxWidth: 272 }}
                // value={eventPostalCode}
                onChange={(event) => {
                  setEventPostalCode(event.target.value);
                }}
              ></input>
              <input
                type="text"
                placeholder="country"
                style={{ maxWidth: 272 }}
                // value={eventCountry}
                onChange={(event) => {
                  setEventCountry(event.target.value);
                }}
              ></input>
            </Stack>
          </Grid>
        </Grid>
        <Button
          style={{ alignSelf: "center", margin: 15 }}
          onClick={async () => {
            await handleCreate();
          }}
        >
          create
        </Button>
      </Dialog>
    </>
  );
};

SimpleDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  currentUser: PropTypes.object.isRequired,
  // isCreation: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default SimpleDialog;
