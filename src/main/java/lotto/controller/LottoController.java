package lotto.controller;

import static lotto.utils.CalculationUtils.isDivisible;
import static lotto.utils.StringUtils.parseInt;
import static lotto.view.ErrorMessage.*;
import static lotto.view.InputMessage.*;
import static lotto.view.InputView.readInput;
import static lotto.view.OutputView.*;

import lotto.domain.Lotto;
import lotto.service.LottoService;
import lotto.utils.CalculationUtils;
import java.util.ArrayList;
import java.util.List;

public class LottoController {
    public static final int ONE_LOTTO_PRICE = 1000;
    private LottoService lottoService;
    private Lotto answer;

    public LottoController() {
        this.lottoService = new LottoService();
    }

    public int receiveMoney() {
        String userInput = readInput(ENTER_TOTAL_PURCHASE_MONEY.getInputMessage());
        validateReceivedMoney(userInput);
        return parseInt(userInput);
    }

    public int receiveMoneyUntilPass() {
        return receiveInputUntilPass(this::receiveMoney);
    }

    public int registerBonusNumber() {
        String userInput = readInput(ENTER_BONUS_NUMBER.getInputMessage());
        validateLottoNumber(userInput);
        validateBonusLottoNumber(userInput, answer);
        return parseInt(userInput);
    }


    public int registerBonusNumberUntilPass() {
        return receiveInputUntilPass(this::registerBonusNumber);
    }

    public Lotto registerWinningLottoCombination() {
        String userInput = readInput(ENTER_WINNING_LOTTO_NUMBER.getInputMessage());
        validateWinningLottoCombination(userInput);
        List<Integer> lottoCombination = userInputToLottoCombination(userInput);
        Lotto result = new Lotto(lottoCombination);
        setAnswer(result);

        return result;
    }

    public Lotto registerWinningLottoCombinationUntilPass() {
        return receiveInputUntilPass(this::registerWinningLottoCombination);
    }

    public List<Lotto> generateLottoList(int count) {
        List<Lotto> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            result.add(lottoService.generateLotto());
        }

        return result;
    }

    public void showPurchaseResult(List<Lotto> lottoList, int totalPurchaseAmount) {
        int purchaseCount = totalPurchaseAmount / ONE_LOTTO_PRICE;
        String purchaseResult = lottoService.makePurchaseResultOutputStatement(lottoList, purchaseCount);
        printResult(purchaseResult);
    }

    public void showStatisticsResult(List<Lotto> lottoList, Lotto answer, int bonusNumber) {
        String result = lottoService.makeStatisticsResultOutputStatement(lottoList, answer, bonusNumber);
        printResult(result);
    }

    private void validateReceivedMoney(String userInput) {
        // 숫자 인지 검증
        int number = parseInt(userInput);

        if (!isDivisible(number, ONE_LOTTO_PRICE)) {
            throw new IllegalArgumentException(RECEIVED_MONEY_NOT_MULTIPLE_OF_1000.getErrorMessage());
        }
    }

    private void validateLottoNumber(String userInput) {
        int number = parseInt(userInput);

        if (!CalculationUtils.isNumberInRange(number, 1, 45)) {
            throw new IllegalArgumentException(LOTTO_NUMBER_RANGE_IS_BETWEEN_ONE_AND_FORTYFIVE.getErrorMessage());
        }
    }

    private void validateBonusLottoNumber(String userInput, Lotto answer) {
        int number = parseInt(userInput);
        if (answer.isNumberIn(number)) {
            throw new IllegalArgumentException(THAT_NUMBER_IS_ALREADY_CONTAINS_ANSWER_COMBINATION.getErrorMessage());
        }
    }

    private void validateWinningLottoCombination(String userInput) {
        String[] strArr = userInput.replace(" ", "").split(",");

        for (String str : strArr) {
            validateLottoNumber(str); // 숫자인지 체크, 1~45인지 체크
        }
    }

    private List<Integer> userInputToLottoCombination(String userInput) {
        List<Integer> result = new ArrayList<>();
        String[] strArr = userInput.replace(" ", "").split(",");

        for (String str : strArr) {
            result.add(parseInt(str)); // 숫자인지 체크, 1~45인지 체크
        }

        result.sort(null);

        return result;
    }

    public <T> T receiveInputUntilPass(ExceptionSupplier<T> inputMethod) {
        T result = null;

        while (true) {
            try {
                result = inputMethod.get();
                break;
            } catch (IllegalArgumentException e) {
                printResult(e.getMessage());
            }
        }

        return result;
    }

    public Lotto getAnswer() {
        return answer;
    }

    public void setAnswer(Lotto answer) {
        this.answer = answer;
    }

}