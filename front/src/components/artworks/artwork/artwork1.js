import React from "react";
import {
  Card,
  CardMedia,
  CardContent,
  CardActions,
  Typography,
} from "@mui/material";
import { MdAddShoppingCart } from "react-icons/md";
import { IconButton } from "@mui/material";
import useStyles from "./styles";

const Artwork = ({ artwork }) => {
  const classes = useStyles();
  return (
    <Card className={classes.root}>
      <CardMedia
        className={classes.media}
        image={artwork.image}
        title={artwork.name}
      />
      <CardContent>
        <div className={classes.cardContent}>
          <Typography variant="h5" gutterBottom>
            {artwork.name}
          </Typography>
          <Typography variant="h5">{artwork.price}</Typography>
        </div>
        <Typography variant="body2" color="textSecondary">
          {artwork.description}
        </Typography>
      </CardContent>
      <CardActions disableSpacing className={classes.cardActions}>
        <IconButton aria-label="Add to cart">
          <MdAddShoppingCart />
        </IconButton>
      </CardActions>
    </Card>
  );
};

export default Artwork;
