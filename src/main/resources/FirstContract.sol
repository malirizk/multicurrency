pragma solidity ^0.4.0;

contract FirstContract {
    
    mapping (address => uint) private balances;
    
    address public owner;
    
    address private xAddress = 0x7D196004926D7B60d0f7bBc20d2Ce875800b53DF;
    address private yAddress = 0x44791058f6a84ed0E93dE74E15025f31A8cD0FaD;
    address private zAddress = 0xDbEDA162C99bCbe9F716755d125CB04a49866b98;
    
    bool isXPaid = false;
    bool isYPaid = false;
    bool isZPaid = false;
    
    function FirstContract() public {
        owner = msg.sender;
    }
    
    function transfer() public payable {
        require(msg.sender.balance >= msg.value);
        
         if (yAddress == msg.sender) {
            balances[msg.sender] -= msg.value;
            isYPaid = true;
        }
        
        if (zAddress == msg.sender) {
            balances[msg.sender] -= msg.value;
            isZPaid = true;
        }
        
        if (!isXPaid && isYPaid && isZPaid) {
            xAddress.transfer(address(this).balance);
            isXPaid = true;
        }
    }
    
    function getBalance() constant public returns (uint) {
        return xAddress.balance;
    }
    
}