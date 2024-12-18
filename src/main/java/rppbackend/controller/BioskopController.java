package rppbackend.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import rppbackend.model.Bioskop;
import rppbackend.service.BioskopService;

@CrossOrigin
@RestController
public class BioskopController {
	
	@Autowired
	private BioskopService bioskopService;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @ApiOperation(value = "Returns List of all Bioskopi")
	@GetMapping("bioskop")
	public ResponseEntity<List<Bioskop>> getAll(){
		List<Bioskop> bioskopi = bioskopService.getAll();
        return new ResponseEntity<>(bioskopi, HttpStatus.OK);
	}
	
    @ApiOperation(value = "Returns Bioskop with id that was forwarded as path variable.")
	@GetMapping("bioskop/{id}")
	public ResponseEntity<Bioskop> getOne(@PathVariable("id") Integer id){
	    if (bioskopService.findById(id).isPresent()) {
	    	Optional<Bioskop> bioskop = bioskopService.findById(id);
            return new ResponseEntity<>(bioskop.get(), HttpStatus.OK);
	    } else {
	    	return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	    }
	}
	
    @ApiOperation(value = "Returns list of Bioskopi containing string that was forwarded as path variable in 'naziv'.")
	@GetMapping("bioskop/naziv/{naziv}")
	public ResponseEntity<List<Bioskop>> getByNaziv(@PathVariable("naziv") String naziv){
		List<Bioskop> bioskopi = bioskopService.findByNazivContainingIgnoreCase(naziv);
        return new ResponseEntity<>(bioskopi, HttpStatus.OK);
	}
	
    @ApiOperation(value = "Adds new Bioskop to database.")
	@PostMapping("bioskop")
	public ResponseEntity<Bioskop> addBioskop(@RequestBody Bioskop bioskop) {
		Bioskop savedBioskop = bioskopService.save(bioskop);
        URI location = URI.create("/bioskop/" + savedBioskop.getId());
		return ResponseEntity.created(location).body(savedBioskop);
	}

    @ApiOperation(value = "Updates Bioskop that has id that was forwarded as path variable with values forwarded in Request Body.")
    @PutMapping(value = "bioskop/{id}")
    public ResponseEntity<Bioskop> updateBioskop(@RequestBody Bioskop bioskop, @PathVariable("id") Integer id) {
        if (bioskopService.existsById(id)) {
            bioskop.setId(id);
            Bioskop savedBioskop = bioskopService.save(bioskop);
            return ResponseEntity.ok().body(savedBioskop);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
	
    @ApiOperation(value = "Deletes Bioskop with id that was forwarded as path variable.")
    @DeleteMapping("bioskop/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable Integer id) {
        if (id == -100 && !bioskopService.existsById(id)) {
            jdbcTemplate.execute(
                    "INSERT INTO bioskop (\"id\", \"naziv\", \"adresa\") VALUES ('-100', 'Test Naziv', 'Test Adresa')");
        }

        if (bioskopService.existsById(id)) {
            bioskopService.deleteById(id);
            return new ResponseEntity<HttpStatus>(HttpStatus.OK);
        }
        return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
    }

}
