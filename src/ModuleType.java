/**
 * Created by jesus on 06/02/2017.
 */
public enum ModuleType {
    CUSTOMER_MANAGEMENT_MODULE(1), PROCESS_MANAGER_MODULE(2), QUERY_PROCESSING_MODULE(3), TRANSACTION_AND_DATA_ACCESS_MODULE(4), EXECUTION_MODULE(5);

    private int moduleNumber;

    ModuleType(int moduleNumber){
        this.moduleNumber = moduleNumber;
    }

    public int getModuleNumber(){
        return moduleNumber;
    }
}
