package com.comfandi.phobos.service;



import com.comfandi.phobos.client.ExternalServiceClient;
import com.comfandi.phobos.client.request.ApiInformationDatabase;
import com.comfandi.phobos.client.response.ApiDocumentGenerationResponse;
import com.comfandi.phobos.entity.BillingAccountAmortizationEntity;
import com.comfandi.phobos.entity.BillingAccountEntity;
import com.comfandi.phobos.entity.UserEntity;
import com.comfandi.phobos.mapper.InformationDatabaseMapper;
import com.comfandi.phobos.mapper.UserMapper;
import com.comfandi.phobos.repository.BillingAccountAmortizationRepository;
import com.comfandi.phobos.service.data.BillingAmortizationGroup;
import com.comfandi.phobos.service.dto.UserDto;
import com.comfandi.phobos.service.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class BillingAccountAmortizationService {

    @Autowired
    private BillingAccountAmortizationRepository billingAccountAmortizationRepository;

    @Autowired
    private BillingAccountServices billingAccountServices;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InformationDatabaseMapper informationDatabaseMapper;

    @Autowired
    private ExternalServiceClient externalServiceClient;

    public List<BillingAccountAmortizationEntity> findAmortizationByBillingAccount(Long billingAccountId,Long programId,Long portfolioId, LocalDate amortizationDate){
        return billingAccountAmortizationRepository.findByAccountId(billingAccountId,programId,portfolioId,amortizationDate);
    }

    public BillingAccountAmortizationEntity saveAmortization(BillingAccountAmortizationEntity amortizationEntity){
        return billingAccountAmortizationRepository.save(amortizationEntity);
    }


    public List<BillingAccountAmortizationEntity> generateAmortizations(LocalDate amortizationDate){
        List<BillingAccountAmortizationEntity> amortizationEntities=new ArrayList<>();
        List<BillingAccountAmortizationEntity> amortizationListSaved=new ArrayList<>();
        List<BillingAccountEntity> amortizableBillingAccounts=billingAccountServices.getAllPendingAmortizableBillingAccounts();
        List<UserDto> usersData= new ArrayList<>();
        for(BillingAccountEntity account:amortizableBillingAccounts){
            List<UserEntity> users=userService.getUsersByAccount(account.getBillingAccountId().longValue());
            UserType userType = UserType.fromValue(users.getFirst().getRegional().getName());
            Map<BillingAmortizationGroup,List<UserEntity>> group = users.stream().collect(Collectors.groupingBy(x ->
                    new BillingAmortizationGroup(x.getRegional().getName(),x.getCourse().getId(),
                            x.getCourse().getPortfolio().getId().longValue(),x.getCourse().getModality().getId())));
            for(Map.Entry<BillingAmortizationGroup,List<UserEntity>> entry:group.entrySet()){
                List<BillingAccountAmortizationEntity> amortizationList=findAmortizationByBillingAccount(account.getBillingAccountId().longValue(),
                        entry.getKey().getProgramId(),
                        entry.getKey().getPortfolioId(),
                        amortizationDate);
                int amortizationListSize=amortizationList.size();
                if(amortizationListSize<3){
                    BillingAccountAmortizationEntity newAmortization=new BillingAccountAmortizationEntity();
                    newAmortization.setBillingAccount(account);
                    newAmortization.setAmortizationNumber(amortizationList.size()+1);
                    newAmortization.setPortfolio(entry.getValue().getFirst().getCourse().getPortfolio());
                    newAmortization.setCourse(entry.getValue().getFirst().getCourse());
                    newAmortization.setMonth(amortizationDate.getMonthValue());
                    newAmortization.setYear(amortizationDate.getYear());
                    newAmortization.setAmortizationDate(amortizationDate);
                    if(amortizationListSize==0){
                        if(entry.getValue().getFirst().getCourse().getPortfolio().getDownPayment1()!=null){
                            newAmortization.setAmortizationNumber(1);
                            newAmortization.setPercent(entry.getValue().getFirst().getCourse().getPortfolio().getDownPayment1());
                            newAmortization.setValue(((entry.getValue().stream().mapToDouble(x->x.getCourseFee().doubleValue()).sum())* newAmortization.getPercent())/100);
                            amortizationEntities.add(newAmortization);
                            usersData.addAll(entry.getValue().stream().map(x->{
                                UserDto userDto=userMapper.userEntityToUserDto(x);
                                userDto.setCourseFee(BigDecimal.valueOf((x.getCourseFee().doubleValue()*newAmortization.getPercent())/100));
                                return  userDto;
                            } ).toList());
                        }
                    } else if (amortizationListSize==1) {
                        if(entry.getValue().getFirst().getCourse().getPortfolio().getDownPayment2()!=null){
                            newAmortization.setAmortizationNumber(2);
                            newAmortization.setPercent(entry.getValue().getFirst().getCourse().getPortfolio().getDownPayment2());
                            newAmortization.setValue(((entry.getValue().stream().mapToDouble(x->x.getCourseFee().doubleValue()).sum())* newAmortization.getPercent())/100);
                            amortizationEntities.add(newAmortization);
                            usersData.addAll(entry.getValue().stream().map(x->{
                                UserDto userDto=userMapper.userEntityToUserDto(x);
                                userDto.setCourseFee(BigDecimal.valueOf((x.getCourseFee().doubleValue()*newAmortization.getPercent())/100));
                                return  userDto;
                            } ).toList());
                        }
                    } else {
                        if(entry.getValue().getFirst().getCourse().getPortfolio().getDownPayment3()!=null){
                            newAmortization.setAmortizationNumber(3);
                            newAmortization.setPercent(entry.getValue().getFirst().getCourse().getPortfolio().getDownPayment3());
                            newAmortization.setValue(((entry.getValue().stream().mapToDouble(x->x.getCourseFee().doubleValue()).sum())* newAmortization.getPercent())/100);
                            amortizationEntities.add(newAmortization);
                            usersData.addAll(entry.getValue().stream().map(x->{
                                UserDto userDto=userMapper.userEntityToUserDto(x);
                                userDto.setCourseFee(BigDecimal.valueOf((x.getCourseFee().doubleValue()*newAmortization.getPercent())/100));
                                return  userDto;
                            } ).toList());
                        }
                    }

                }
            }
            List<BillingAccountAmortizationEntity> savedList=saveALlAmortizations(amortizationEntities);
            if(!amortizationEntities.isEmpty()){
                List<ApiInformationDatabase> data= usersData.stream().map(x->informationDatabaseMapper.userDtoToApiInformationDatabase(x)).toList();
                ApiDocumentGenerationResponse response=externalServiceClient.fetchKorlonAmortizeromService(data,UserType.validateProfile(userType),account.getBillingAccountId().longValue());
                if(response!=null){
                    System.out.println("document amortization url "+response.getExcelDocumentUrl());
                    amortizationListSaved.forEach(x-> x.setAmortizatioUrl1(response.getExcelDocumentUrl()));
                    billingAccountAmortizationRepository.saveAll(savedList);
                }
            }
            amortizationListSaved.addAll(savedList);
        }
        return amortizationListSaved;
    }

    private List<BillingAccountAmortizationEntity> saveALlAmortizations (List<BillingAccountAmortizationEntity> amortizationEntityList){
        List<BillingAccountAmortizationEntity> list=new ArrayList<>();
        for(BillingAccountAmortizationEntity amortizationEntity:amortizationEntityList){
            Optional<BillingAccountAmortizationEntity> optEntity=billingAccountAmortizationRepository.findAmortization(amortizationEntity.getBillingAccount().getBillingAccountId().longValue(),
                    amortizationEntity.getCourse().getId(),amortizationEntity.getPortfolio().getId().longValue(),amortizationEntity.getAmortizationNumber(),amortizationEntity.getValue());
            if(optEntity.isPresent()){
                BillingAccountAmortizationEntity newAmortizationEntity = optEntity.get();
                newAmortizationEntity.setValue(amortizationEntity.getValue());
                list.add(billingAccountAmortizationRepository.save(newAmortizationEntity));
            }else{
                list.add(billingAccountAmortizationRepository.save(amortizationEntity));
            }}
        return list;
    }
}
