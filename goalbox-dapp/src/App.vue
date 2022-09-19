<template>
  <v-app>
    <v-main>
      <!-- <NotSupported/> -->
      <main-view v-if="appSupported"/>
      <not-supported v-else/>
    </v-main>
  </v-app>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import MainView from './components/MainView.vue'
import NotSupported from './components/NotSupported.vue';

export default defineComponent({
  name: 'App',

  components: {
    MainView,
    NotSupported
  },

  data () {
    return {
      appSupported: false
    }
  },

  mounted() {
    if (typeof (window as any).ethereum !== undefined) {
      this.appSupported = true;
    }
  }
})
</script>