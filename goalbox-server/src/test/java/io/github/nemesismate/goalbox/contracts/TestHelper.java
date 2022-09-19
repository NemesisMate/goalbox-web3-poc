package io.github.nemesismate.goalbox.contracts;

import lombok.Builder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;

@Builder
public class TestHelper<C extends Contract> {

    private final Web3j web3j;
    private final TransactionManager transactionManager;
    private final ContractGasProvider gasProvider;

    private final DeployFunction<C> contractDeployer;

    public C deployNewContract() throws Exception {
        return contractDeployer.apply(web3j, transactionManager, gasProvider);
    }

    public void givenContractWithBalance(Contract contract, BigInteger balance) throws Exception {
        givenAddressWithBalance(contract.getContractAddress(), balance);
    }

    public void givenAddressWithBalance(String address, BigInteger balance) throws Exception {
        if(!getBalance(address).equals(BigInteger.ZERO)) {
            throw new IllegalStateException("The given address already has a balance");
        }
        transactionManager.sendTransaction(gasProvider.getGasPrice(""), gasProvider.getGasLimit(""), address, "", balance);
    }

    public BigInteger getBalance(String address) throws Exception {
        return asNumeric(web3j.ethGetBalance(address, LATEST).send());
    }

    public BigInteger getBalance(Contract contract) throws Exception {
        return getBalance(contract.getContractAddress());
    }

    public BigInteger gasCosts(BigInteger gasUsed) {
        return gasUsed.multiply(gasProvider.getGasPrice(""));
    }


    /**
     * {@link EthGetBalance#getBalance()} ends up using {@link Numeric#isValidHexQuantity(String)} which seems to be validating wrong.
     */
    public static BigInteger asNumeric(EthGetBalance balance) {
        return new BigInteger(balance.getResult().substring(2), 16);
    }

    @FunctionalInterface
    public interface DeployFunction<C extends Contract> {

        C apply(Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) throws Exception;
    }

}
