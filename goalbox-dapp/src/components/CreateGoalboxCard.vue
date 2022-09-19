<template>
    <v-card class="ma-2" width="300">
        <v-card-title>
            New goal box
            <v-tooltip v-if="quantity">
                <template v-slot:activator="{ props }">
                    <v-badge :content="'+' + quantity" v-bind="props">
                        <v-progress-circular v-bind="props" :size="20" :width="3" indeterminate/>
                    </v-badge>
                </template>
                <span>{{quantity}} goalbox creation in progress</span>
            </v-tooltip>
        </v-card-title>

        <div>
            <v-divider></v-divider>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn variant="outlined" color="primary" @click.stop="form=!form">{{form ? "Close" : "Create"}}</v-btn>
                <v-spacer></v-spacer>
            </v-card-actions>


            <v-container v-if="form">
                <v-row justify="center">
                    <v-text-field label="Name" type="text" required v-model="boxName"></v-text-field>
                </v-row>
                <v-row justify="center">
                    <v-text-field label="Goal" type="number" suffix="ETH" required v-model="goal">
                    </v-text-field>
                </v-row>
                <v-spacer></v-spacer>
                <v-btn color="primary" text @click="form=false; create(boxName, goal, accountAddress)">Create</v-btn>
            </v-container>
        </div>
    </v-card>
</template>

<script lang='ts'>
import { defineComponent } from 'vue'
import Web3 from 'web3';
import { Goalbox } from '../models/Goalbox';
import { Metadata } from '../constants/Metadata';

export default defineComponent({
    name: 'CreateGoalboxCard',

    props: {
        accountAddress: { type: String, required: true },
    },

    data() {
        return {
            form: false,

            boxName: "",
            goal: 0,

            quantity: 0,
        }
    },

    methods: {
        create(name: string, goal: number, ownerAddress: string) {
            this.quantity++;

            return fetch(`${Metadata.API_URL}/goalbox`, {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name: name,
                    goal: Web3.utils.toWei(goal.toString(), "ether"),
                    ownerAddress: ownerAddress
                })
            }).then(response => {
                if (!response.ok) {
                    return Promise.reject(new Error("Couldn't create Goalbox"))
                }
                return response.json();
            }).then(json => json as Goalbox)
                .then(goalbox => this.$emit('add-goalbox', goalbox))
                .finally(() => this.quantity--);
        },
    },

    mounted() {
    }
})
</script>
