package io.github.nemesismate.goalbox.contracts;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.EVMTest;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;

import static java.math.BigInteger.ZERO;
import static org.assertj.core.api.Assertions.assertThat;

@EVMTest
public class GoalboxTest {

    static final String owner = "0xfe3b557e8fb62b89f4916b721be55ceb828dbd73";

    static TestHelper<GoalboxContract> testHelper;
    GoalboxContract contract;

    @BeforeAll
    public static void setup(Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) {
        testHelper = TestHelper.<GoalboxContract>builder()
                .web3j(web3j)
                .transactionManager(transactionManager)
                .gasProvider(gasProvider)
                .contractDeployer(GoalboxTest::deployNewContract)
                .build();
    }

    @BeforeEach
    public void setupContract() throws Exception {
        contract = testHelper.deployNewContract();
    }

    @Test
    public void whenGetBalanceFromGoalboxThenItMatchesContractValue() throws Exception {
        var initialBalance = BigInteger.TEN;
        testHelper.givenContractWithBalance(contract, initialBalance);

        var value = contract.getValue().send();

        assertThat(value).isEqualTo(getBalance(contract)).isEqualTo(initialBalance);
    }

    @Test
    public void whenContractIsDeployedThenOwnerIsSet() throws Exception {
        assertThat(contract.owner().send()).isEqualTo(owner);
    }

    @Test
    public void whenFundThenBalanceIsTransferredFromOwner() throws Exception {
        var ownerBalance = getOwnerBalance();
        var contractBalance = getContractBalance();
        var wei = BigInteger.valueOf(7);

        var receipt = contract.fund(wei).send();

        assertThat(getOwnerBalance()).isEqualTo(ownerBalance.subtract(wei).subtract(gasCosts(receipt)));
        assertThat(getContractBalance()).isEqualTo(contractBalance.add(wei));
    }

    @Test
    public void whenFundThenFundEventIsTriggered() throws Exception {
        var initialBalance = BigInteger.TEN;
        var wei = BigInteger.valueOf(7);

        testHelper.givenContractWithBalance(contract, initialBalance);

        var receipt = contract.fund(wei).send();
        var events = GoalboxContract.getFundEvents(receipt);
        var event = events.get(0);

        assertThat(events.size()).isEqualTo(1);
        assertThat(event.balance).isEqualTo(initialBalance.add(wei));
        assertThat(event.value).isEqualTo(wei);
        assertThat(event.from).isEqualTo(owner);
    }

    @Test
    public void whenSettleThenValueRemains() throws Exception {
        var initialBalance = BigInteger.TEN;
        testHelper.givenContractWithBalance(contract, initialBalance);

        contract.settle().send();

        assertThat(contract.getValue().send()).isEqualTo(initialBalance);
    }

    @Test
    public void whenSettleThenContractIsMarkedAsSettled() throws Exception {
        contract.settle().send();

        assertThat(contract.isSettled().send()).isEqualTo(true);
    }

    @Test
    public void whenSettleThenBalanceIsTransferredToOwner() throws Exception {
        var initialBalance = BigInteger.TEN;
        testHelper.givenContractWithBalance(contract, initialBalance);

        var ownerBalance = getOwnerBalance();
        var contractBalance = getContractBalance();

        var receipt = contract.settle().send();

        assertThat(getOwnerBalance()).isEqualTo(ownerBalance.add(contractBalance.subtract(gasCosts(receipt))));
        assertThat(getContractBalance()).isEqualTo(ZERO);
    }

    @Test
    public void whenSettleThenSettleEventIsTriggered() throws Exception {
        var initialBalance = BigInteger.TEN;

        testHelper.givenContractWithBalance(contract, initialBalance);

        var receipt = contract.settle().send();
        var events = GoalboxContract.getSettleEvents(receipt);
        var event = events.get(0);

        assertThat(events.size()).isEqualTo(1);
        assertThat(event.value).isEqualTo(initialBalance);
    }


    @Test
    public void whenTransferThenBalanceIsTransferredToOtherContract() throws Exception {
        var otherContract = testHelper.deployNewContract();

        testHelper.givenContractWithBalance(contract, new BigInteger("1000000000000000000"));

        var ownerBalance = getOwnerBalance();
        var contractBalance = getContractBalance();
        var otherContractBalance = getBalance(otherContract);

        var wei = BigInteger.valueOf(13);

        var receipt = contract.transfer(otherContract.getContractAddress(), wei).send();

        assertThat(getOwnerBalance()).isEqualTo(ownerBalance.subtract(gasCosts(receipt)));
        assertThat(getContractBalance()).isEqualTo(contractBalance.subtract(wei));
        assertThat(getBalance(otherContract)).isEqualTo(otherContractBalance.add(wei));
    }

    @Test
    public void whenTransferThenTransferEventIsTriggered() throws Exception {
        var otherContract = testHelper.deployNewContract();
        var initialBalance = BigInteger.TEN;
        var wei = BigInteger.valueOf(3);
        var target = otherContract.getContractAddress();

        testHelper.givenContractWithBalance(contract, initialBalance);

        var receipt = contract.transfer(target, wei).send();
        var events = GoalboxContract.getTransferEvents(receipt);
        var event = events.get(0);

        assertThat(events.size()).isEqualTo(1);
        assertThat(event.value).isEqualTo(wei);
        assertThat(event.to).isEqualTo(target);
        assertThat(event.balance).isEqualTo(initialBalance.subtract(wei));
    }

    @Test
    public void whenTransferThenFundEventIsTriggered() throws Exception {
        var otherContract = testHelper.deployNewContract();
        var wei = BigInteger.valueOf(3);

        testHelper.givenContractWithBalance(contract, BigInteger.TEN);

        var receipt = contract.transfer(otherContract.getContractAddress(), wei).send();
        var events = GoalboxContract.getFundEvents(receipt);
        var event = events.get(0);

        assertThat(events.size()).isEqualTo(1);
        assertThat(event.value).isEqualTo(wei);
        assertThat(event.from).isEqualTo(contract.getContractAddress());
        assertThat(event.balance).isEqualTo(wei);
    }


    private BigInteger getOwnerBalance() throws Exception {
        return testHelper.getBalance(owner);
    }

    private BigInteger getContractBalance() throws Exception {
        return testHelper.getBalance(contract);
    }

    private BigInteger getBalance(GoalboxContract goalbox) throws Exception {
        return testHelper.getBalance(goalbox);
    }

    private BigInteger gasCosts(TransactionReceipt receipt) {
        return gasCosts(receipt.getGasUsed());
    }

    private BigInteger gasCosts(BigInteger gasUsed) {
        return testHelper.gasCosts(gasUsed);
    }

    private static GoalboxContract deployNewContract(Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) throws Exception {
        return GoalboxContract.deploy(web3j, transactionManager, gasProvider, ZERO, owner).send();
    }


}
