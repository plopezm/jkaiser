import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import { CssBaseline, Container, Grid, Typography } from '@material-ui/core';
import './App.css';

import MainMenu from './components/main-menu';


const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex',
  },
  content: {
    flexGrow: 1,
    height: '100vh',
    overflow: 'auto',
  },
  appBarSpacer: theme.mixins.toolbar,  
  container: {
    paddingTop: theme.spacing(4),
    paddingBottom: theme.spacing(4),
  },
}));

function App() {
  const classes = useStyles();

  return (    
    <div className={classes.root}>
      <CssBaseline />
      <MainMenu></MainMenu>
      <main className={classes.content}>
        <div className={classes.appBarSpacer}></div>
        <Container maxWidth="lg" className={classes.container}>
          <Grid container spacing={3}>
          <Grid item xs={12} md={8} lg={9}>
              <Typography>Hello World</Typography>
          </Grid>
          </Grid>
        </Container>
      </main>
    </div>
  );
}

export default App;
