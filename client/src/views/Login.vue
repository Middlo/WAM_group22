<template>
  <div>
    <b-jumbotron header="Caletask" lead="Welcome to the login page prototype">
    </b-jumbotron>

    <b-form id=form1 class="form" @login="onLogin" v-if="showL">
        <b-form-group
            id="input-group-1"
            label="Username:"
            label-for="input-1"
        >
            <b-form-input
            id="input-1"
            v-model="form1.username"
            required
            placeholder="Enter Username"
            ></b-form-input>
        </b-form-group>

        <b-form-group 
            id="input-group-2" 
            label="Password:" 
            label-for="input-2"
        >
            <b-form-input
            id="input-2"
            v-model="form1.password"
            required
            placeholder="Enter Password"
            ></b-form-input>
        </b-form-group>
        
        <b-button type="login" variant="primary">Login</b-button>
        <b-button variant="danger" @click="onReset;showL = false">Register</b-button>

        <b-card class="mt-3" header="Form Data Result">
            <pre class="m-0">{{ form1 }}</pre>
        </b-card>
    </b-form>

    <b-form id=form2 class="form" @register="onRegister" v-if="!showL">
        <b-form-group
            id="input-group-3"
            label="Email:"
            label-for="input-3"
        >
            <b-form-input
            id="input-3"
            v-model="form2.email"
            required
            placeholder="Enter Email"
            ></b-form-input>
        </b-form-group>

        <b-form-group 
            id="input-group-4" 
            label="Username:" 
            label-for="input-4"
        >
            <b-form-input
            id="input-4"
            v-model="form2.rUsername"
            required
            placeholder="Enter Username"
            ></b-form-input>
        </b-form-group>

        <b-form-group 
            id="input-group-5" 
            label="Password:" 
            label-for="input-5"
        >
            <b-form-input
            id="input-5"
            v-model="form2.rPassword"
            required
            placeholder="Enter Password"
            ></b-form-input>
        </b-form-group>
        
        <b-button type="login" variant="primary">Register </b-button>
        <b-button variant="danger" @click="onReset;showL = true">Already Registered</b-button>

        <b-card class="mt-3" header="Form Data Result">
            <pre class="m-0">{{ form2 }}</pre>
        </b-card>
    </b-form>   
  </div>
</template>

<script>
// @ is an alias to /src
import { Api } from '@/Api'

export default {
  name: 'Login',
  data() {
    return {
        message: '',
        showL: true,
        form1: {
            username: '',
            password: ''
        },
        form2: { 
            email: '',
            rUsername: '',
            rPassword: ''
        }
    }
  },
  mounted() {
    this.getMessage()
  },
  methods: {
    getMessage() {
      Api.get('/')
        .then(response => {
          this.message = response.data.message
        })
        .catch(error => {
          this.message = error
        })
    },
    onLogin(evt) {
        evt.preventDefault()
        alert(JSON.stringify(this.form1))
    },
    onRegister(evt) {
        evt.preventDefault()
        alert(JSON.stringify(this.form2))
    },
    onReset(evt) {
        evt.preventDefault()
        this.form1.username = ''
        this.form1.password = ''
        this.form2.email = ''
        this.form2.rUsername = ''
        this.form2.rPassword = ''
    }
  }
}
</script>

<style>
.form {
    text-align: center;
    margin-left: 35%;
    margin-right: 35%;
}
</style>
