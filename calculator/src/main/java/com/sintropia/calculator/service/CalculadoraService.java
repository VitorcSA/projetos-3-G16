import org.springframework.stereotype.Service;
import com.sintropia.calculator.dto.CalculoRequestDTO;
import com.sintropia.calculator.dto.CalculoResponseDTO;

@Service
public class CalculadoraService {

    public CalculoResponseDTO calcular(CalculoRequestDTO request) {

        double emissaoFisico =
                (request.getPesoPvc() / 1000 * 2.7) +
                (request.getDistanciaTransporteKm() * 0.21);

        double emissaoDigital =
                (request.getConsumoEnergiaKwh() * 0.084);

        double diferenca = emissaoFisico - emissaoDigital;

        double percentualReducao =
                (diferenca / emissaoFisico) * 100;

        CalculoResponseDTO response = new CalculoResponseDTO();
        response.setEmissaoCartaoFisico(emissaoFisico);
        response.setEmissaoCartaoDigital(emissaoDigital);
        response.setDiferenca(diferenca);
        response.setPercentualReducao(percentualReducao);

        return response;
    }
}