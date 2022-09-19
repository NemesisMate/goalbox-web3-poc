<template>
    <v-select center :items="accounts" dense solo v-model="selectedAccount" :loading="loadingAccounts"></v-select>
    
    <div v-if="loadingMetadataError || loadingAccountsError">
        <v-alert v-if="loadingMetadataError" color="deep-orange" icon="mdi-fire" title="Couldn't load your Goalboxes"
            variant="outlined">Please, reload the page or try with another account</v-alert>
        <v-alert v-if="loadingAccountsError" color="deep-orange" icon="mdi-fire" title="Couldn't load your accounts"
            variant="outlined">Please, make sure you have Metamask installed and you are connected to the network</v-alert>
    </div>
    <div v-else-if="selectedAccount && !loadingMetadata">
        <v-card class="d-flex flex-wrap justify-center" flat>
            <goalbox-card v-for="goalbox in goalboxes" :account-address="selectedAccount"
                :goalbox="goalbox" @open-transfer="openTransferDialog(goalbox)" />
        </v-card>
        <v-card class="d-flex flex-wrap justify-center" flat>
            <create-goalbox-card :account-address="selectedAccount" @add-goalbox="addGoalbox" />
        </v-card>
        <v-dialog v-model="transferDialog">
            <transfer-panel :account-address="selectedAccount" :available-goalboxes="goalboxes" :source="transferSource" @transfer-initiated="transferDialog=false"/>
        </v-dialog>
    </div>
</template>

<script lang='ts'>
import { defineComponent, provide } from 'vue'
import Web3 from 'web3'
import { Goalbox } from '../models/Goalbox';
import GoalboxCard from './GoalboxCard.vue';
import TransferPanel from './TransferPanel.vue';
import CreateGoalboxCard from './CreateGoalboxCard.vue';
import InjectionKeys from '../types/InjectionKeys';
import { Metadata } from '../constants/Metadata';

const web3 = new Web3((window as any).ethereum);

export default defineComponent({
    name: "MainView",

    setup() {
        provide(InjectionKeys.Web3, web3);
    },

    data() {
        return {
            loadingAccounts: true,
            loadingAccountsError: false,
            selectedAccount: "",
            accounts: new Array<string>(),

            transferDialog: false,
            transferSource: {} as Goalbox,


            loadingMetadata: true,
            loadingMetadataError: false,
            goalboxes: new Array<Goalbox>(),
            
        };
    },

    methods: {
        loadGoalboxes(accountAddress: string) {
            this.loadingMetadata = true;
            return fetch(`${Metadata.API_URL}/goalbox?ownerAddress=${accountAddress}`)
                .then(response => {
                    if (!response.ok) {
                        return Promise.reject(new Error("Couldn't load Goalboxes"));
                    }
                    return response.json();
                })
                .then(json => json as Goalbox[])
                .then(goalboxes => {
                    this.goalboxes = goalboxes;
                    this.sortGoalboxes();
                }).catch(error => this.loadingMetadataError = true)
                .finally(() => this.loadingMetadata = false);
        },

        addGoalbox(goalbox: Goalbox) {
            this.goalboxes.push(goalbox);
            this.sortGoalboxes();
        },

        handleAccountChange(accounts: string[]) {
            this.loadingAccounts = false;
            this.loadingAccountsError = false;
            this.accounts = accounts.map(account => account.toLowerCase());
            this.selectedAccount = accounts.length === 1 ? this.accounts[0] : "";
        },

        openTransferDialog(source: Goalbox) {
            this.transferSource = source;
            this.transferDialog = true;
        },

        sortGoalboxes() {
            this.goalboxes.sort((a, b) => a.name.localeCompare(b.name))
        }
    },

    watch: {
        selectedAccount(selected: string, previous: string) {
            this.loadGoalboxes(selected);
        },
    },

    mounted() {
        (window as any).ethereum.on('accountsChanged', this.handleAccountChange);

        web3.eth.requestAccounts()
            .then(this.handleAccountChange)
            .catch(error => this.loadingAccountsError = true);
    },

    components: { GoalboxCard, CreateGoalboxCard, TransferPanel }
})
</script>
